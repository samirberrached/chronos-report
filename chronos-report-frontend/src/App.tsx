import { Navigate, Route, Routes } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import { ReportProvider } from "./context/ReportContext";
import { AppLayout } from "./layout/AppLayout";
import { LoginPage } from "./pages/LoginPage";
import { DashboardAdminPage } from "./pages/DashboardAdminPage";
import { DashboardFinancialPage } from "./pages/DashboardFinancialPage";
import { DashboardProductPage } from "./pages/DashboardProductPage";

function App() {
  return (
    <AuthProvider>
      <ReportProvider>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/dashboard" element={<AppLayout />}>
            <Route path="admin" element={<DashboardAdminPage />} />
            <Route path="financial" element={<DashboardFinancialPage />} />
            <Route path="product" element={<DashboardProductPage />} />
          </Route>
          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </ReportProvider>
    </AuthProvider>
  );
}

export default App;
