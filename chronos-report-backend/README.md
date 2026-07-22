# Chronos Report — Algorithme de Classification (Analytic Employee Time)

Ce document résume l'implémentation de l'**algorithme de classification** (Phase 5 de la roadmap projet, le cœur du backend), les difficultés rencontrées en cours de route et les solutions retenues. Il est destiné à servir de support d'explication auprès de l'encadrant de stage.

---

## 1. Contexte et objectif

Le projet Chronos Report doit automatiser l'allocation mensuelle du coût de chaque employé sur 5 dimensions analytiques (OrganizationalUnit, Product, ActivityNature, AccountingCode, Ratio), à partir des données de temps de travail (`EmployeeTime`) et des affectations par défaut.

L'objectif de cette étape était de terminer l'**algorithme de classification** (`ClassificationService`), qui pour chaque employé actif sur une période donnée :
1. Applique une allocation manuelle si elle existe (Cas 1),
2. Sinon calcule une allocation de repli si aucun timesheet n'a été saisi (Cas 2),
3. Gère le cas particulier des congés (Cas 3),
4. Ou répartit le temps réellement saisi par code comptable, avec complétion automatique si la saisie est incomplète (Cas 4),

puis génère deux fichiers CSV (`Report` et `Anomalies`).

---

## 2. Étapes d'implémentation

### 2.1 Vérification de l'existant (Brique 1 — calcul de la période)

`ClassificationService.calculateAnalysisPeriod(...)` existait déjà et calcule, pour un `CompanyMember` et une période donnée (format `"M|YY"`), la fenêtre d'analyse exacte en tenant compte des dates de début/fin de contrat. Vérification faite : cette logique correspond exactement à la **Table 1 — Search Between Dates** du document de spécification officiel (les 6 cas de chevauchement de dates).

Un premier test d'intégration (connexion réelle à la base Postgres) a été écrit pour valider que l'environnement (base de données + logique) fonctionnait correctement.

### 2.2 Découverte et correction d'un problème de qualité de données

En testant la Brique 1 sur une vraie donnée, un cas produisait une période **inversée** (date de début après date de fin). Investigation :

- **Cause** : dans la table `company_member`, **1536 lignes sur 10 000** avaient `end_date < start_date`.
- **Origine** : le fichier CSV source (`employee_time_clean.csv`) contenait des lignes où deux périodes d'affectation différentes (probablement départ puis réembauche) se retrouvaient mélangées.
- **Solution retenue** : un script de nettoyage (Python) qui vide `end_date` pour les lignes incohérentes (traitement du contrat comme "toujours actif" plutôt que d'inventer une date), plutôt que d'échanger arbitrairement les deux dates. Un nouveau fichier `employee_time_clean_cleaned.csv` a été généré (l'original n'a pas été modifié), les 5 services d'import ont été repointés dessus, et `company_member` a été rechargé. Résultat vérifié : 0 incohérence restante sur 10 000 lignes.

### 2.3 Implémentation des 4 cas de l'algorithme

Développement de `ClassificationService.classify(...)` avec :
- Les DTOs `ReportLine`, `AnomalyLine`, `ClassificationResult`,
- Les 4 cas métier conformes au spec détaillé fourni par l'encadrant,
- Une gestion de repli pour les valeurs par défaut (OrgUnit/Product/ActivityNature), avec bascule automatique en anomalie si une valeur est introuvable.

### 2.4 Calcul des jours ouvrables (capacité employé)

Aucune table `CountryCalendar` n'existant en base et `Company.country` étant vide pour toutes les entreprises, il a été décidé de **ne pas créer de table calendrier** mais d'utiliser une **API externe gratuite (Nager.Date)** pour récupérer les jours fériés par pays/année, avec mise en cache mémoire. Pays configuré par défaut : **Tunisie (`TN`)**, dans `application.properties` (`chronos.classification.default-country-code`).

### 2.5 Génération des fichiers CSV

`ReportGenerationService` boucle sur tous les `CompanyMember` actifs d'une période, appelle la classification pour chacun, puis écrit `Analytic_EmployeeTime_Report_MM_AAAA.csv` et `Analytic_EmployeeTime_Anomalies_MM_AAAA.csv`.

### 2.6 Tests

- 5 tests unitaires sur le calcul de période (Brique 1),
- 7 tests unitaires sur les 4 cas de classification (avec un contexte construit en mémoire, sans base de données),
- 1 test d'intégration validant la connexion réelle à Postgres.

---

## 3. Difficultés rencontrées et solutions

### 3.1 `EmployeeTime` ne semblait pas lié à `AccountingCode`

**Problème** : l'entité `EmployeeTime` référence `Employee`, `Activity` et `OrganizationalUnit`, mais pas directement `AccountingCode` — alors que l'algorithme a besoin de regrouper les timesheets par code comptable.

**Solution** : investigation du schéma a montré que le lien existe déjà indirectement : `EmployeeTime → Activity → Phase → AccountingCode` (`Phase` a un FK `accounting_code_id`). Vérifié en base : 100% des lignes `employee_time` résolvent correctement vers un `AccountingCode` via cette chaîne. Aucune modification de schéma nécessaire.

### 3.2 100% des employés tombaient en anomalie (bug caché par les données)

**Problème** : lors d'un premier test réel sur un mois complet, **7333 employés sur 7333** généraient une anomalie — un taux de 100% clairement anormal.

**Cause** : les tables `employee_by_product` et `employee_by_activity_nature` avaient leurs colonnes `start_date`/`end_date` **NULL sur les 10 000 lignes** (le service d'import ne les renseigne jamais, et le CSV source ne fournit pas ces dates pour ces deux dimensions). Or en SQL, `NULL <= date` n'est jamais vrai : la requête de recherche "produit par défaut actif sur la période" excluait donc systématiquement toutes les lignes.

**Solution** : décision (avec l'encadrant) de considérer que ces deux tables ne portent pas de dimension temporelle utile — les colonnes `start_date`/`end_date` ont été **supprimées** de ces deux tables (en base et dans le code), et les requêtes simplifiées en conséquence. Résultat après correction : 2 vraies anomalies sur 7333 employés (taux cohérent).

### 3.3 Un test d'intégration invisible pour `mvn test`

**Problème** : un test de connexion DB était écrit comme classe imbriquée (`static class IntegrationTest`) à l'intérieur d'un autre fichier de test. Il passait quand on le lançait explicitement, mais **Maven Surefire ne l'exécutait jamais** lors d'un `mvn test` classique (seuls 12 tests s'exécutaient au lieu de 13).

**Solution** : extraction de cette classe en fichier top-level indépendant (`ClassificationServiceIntegrationTest.java`), désormais bien pris en compte par la suite de tests standard.

### 3.4 Performance : de 5+ minutes à 2,2 secondes

**Problème** : la génération du rapport pour un mois (~7333 employés) prenait plus de 5 minutes — inacceptable pour un usage futur depuis une interface admin qui doit être quasi instantanée.

**Diagnostic et corrections en 3 étapes :**

| Étape | Cause identifiée | Solution | Gain |
|---|---|---|---|
| 1 | Une requête SQL par employé (4 à 6 requêtes × 7333 employés = ~40 000 requêtes) | Préchargement de toute les données de référence en 5 requêtes bulk, regroupées en mémoire par employé (`ClassificationContext` / `ClassificationContextLoader`) | 5+ min → ~4,8 s |
| 2 | Les associations JPA `@ManyToOne` sont `EAGER` par défaut, mais Hibernate ne les *joint* pas automatiquement sans `JOIN FETCH` explicite — même en bulk, chaque ligne déclenchait une requête séparée par association (N+1 caché) | Ajout de `JOIN FETCH` explicite sur toutes les requêtes bulk | ~4,8 s → ~4,0 s |
| 3 | Le chargement de `employee_time` (~58 000 lignes/mois) hydratait des entités JPA complètes (7 tables jointes), coûteux même sans N+1 | Remplacement par une **projection DTO légère** (`EmployeeTimeProjection`) ne portant que les colonnes scalaires réellement utilisées | ~4,0 s → **~2,2 s** |

Cette investigation a été menée en mesurant précisément chaque requête individuellement (et en comparant au temps d'exécution SQL brut via `EXPLAIN ANALYZE`), ce qui a permis d'identifier la vraie cause plutôt que d'optimiser au hasard.

---

## 4. Choix techniques notables

- **Pas de table `CountryCalendar`** : calcul des jours ouvrables via l'API publique **Nager.Date**, avec mise en cache mémoire par pays/année. Pays par défaut : Tunisie (`TN`), configurable dans `application.properties`.
- **Pas de bibliothèque CSV externe** : écriture manuelle des fichiers CSV, cohérente avec le style déjà utilisé dans les services d'import existants.
- **Contexte de classification** (`ClassificationContext`) : sépare le chargement des données (coûteux, à faire une fois) du calcul métier (rapide, par employé), ce qui a été la clé de l'optimisation de performance.

---

## 5. Fichiers créés / modifiés

**Nouveaux fichiers (`com.vermeg.classification`) :**
- `ClassificationContext.java`, `ClassificationContextLoader.java`
- `ReportGenerationService.java`
- `dto/ReportLine.java`, `dto/AnomalyLine.java`, `dto/ClassificationResult.java`, `dto/DateRange.java`
- `calendar/WorkingDaysService.java`, `calendar/NagerDateWorkingDaysService.java`

**Nouveau fichier (`com.vermeg.entity.employeetime`) :**
- `EmployeeTimeProjection.java`

**Fichiers modifiés :**
- `ClassificationService.java` (implémentation complète des 4 cas)
- `CompanyMemberRepository.java`, `OrganizationalAssignmentRepository.java`, `OrganizationalUnitMemberRepository.java`, `EmployeeByProductRepository.java`, `EmployeeByActivityNatureRepository.java`, `EmployeeTimeRepository.java` (requêtes `@Query` avec `JOIN FETCH`)
- `EmployeeByProduct.java`, `EmployeeByActivityNature.java` (suppression des colonnes de dates inutilisées)
- Les 5 services d'import (`*ImportService.java`) : chemin repointé vers le CSV nettoyé
- `application.properties` (pays par défaut pour le calcul de capacité)

**Tests (`src/test/java/com/vermeg/classification/`) :**
- `ClassificationServiceTest.java` (Brique 1)
- `ClassificationServiceCasesTest.java` (4 cas)
- `ClassificationServiceIntegrationTest.java` (connexion DB réelle)

---

## 6. Ce qui reste à faire (hors périmètre de cette étape)

- Endpoint REST pour déclencher la génération du rapport depuis l'interface (Phase 6/7 de la roadmap)
- Tables `ReportExecution` / `ReportAnomaly` pour l'historique des générations
- Le Cas 1 (affectation directe) et le Cas 3 (congés) sont implémentés mais non éprouvés sur données réelles : le jeu de données actuel n'a ni `OrganizationalAssignment` ni `ActivityNature = HOLIDAYS`
- Frontend / dashboards (Phase 7)
- Intégration IA (Phase 9, explicitement hors périmètre pour l'instant)
