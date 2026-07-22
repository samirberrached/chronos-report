// Palette de graphiques — méthode de la skill dataviz.
// Bar chart (magnitude, une seule série) : teinte unique de marque VERMEG.
// Pie chart (identité, plusieurs catégories) : palette catégorielle validée
// (node scripts/validate_palette.js) sur nos surfaces claire/sombre réelles —
// ordre figé, ne jamais réordonner sans revalider.

export const SEQUENTIAL_HUE = "#ED1B2F"; // rouge VERMEG — une seule série, magnitude

export const CATEGORICAL_LIGHT = [
  "#2a78d6", // bleu
  "#008300", // vert
  "#e87ba4", // magenta (sous 3:1 sur fond clair — labels directs obligatoires)
  "#eda100", // jaune (idem)
  "#1baf7a", // aqua (idem)
  "#eb6834", // orange
  "#4a3aa7", // violet
  "#e34948", // rouge
];

export const CATEGORICAL_DARK = [
  "#3987e5",
  "#008300",
  "#d55181",
  "#c98500",
  "#199e70",
  "#d95926",
  "#9085e9",
  "#e66767",
];

export const MAX_PIE_SLICES = 6; // au-delà, on replie le reste dans "Autres"
export const OTHER_LABEL = "Autres";
