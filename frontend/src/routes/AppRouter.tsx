
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";

import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import TestApiComponent from "../components/TestApiComponent";

import Navbar from "../components/Navbar/Navbar";
import TicketsPage from "../pages/TicketPage/TicketsPage.tsx";
import RouteSearchPage from "../pages/RouteSearchPage/RouteSearchPage.tsx";
import TransactionList from "../components/TransactionList/TransactionList";
import RegisterPage from "../pages/RegisterPage/RegisterPage";
import LoginPage from "../pages/LoginPage/LoginPage";
import HomePage from "../components/HomePage/HomePage";
import AdminRoute from "../components/Route/AdminRoute";
import EmployeeRoute from "../components/Route/EmployeeRoute";
import PrivateRoute from "../components/Route/PrivateRoute";
import PublicRoute from "../components/Route/PublicRoute";
import StaffRoute from "../components/Route/StaffRoute";
import AdminSchedulesPage from "../pages/AdminSchedulesPage/AdminSchedulesPage";
import AdminUserRolesPage from "../pages/AdminUserRolesPage/AdminUserRolesPage";
import DepositPage from "../pages/DepositPage/DepositPage.tsx";
import EmployeeSchedulePage from "../pages/EmployeeSchedulePage/EmployeeSchedulePage";
import RouteCreatePage from "../pages/RouteCreatePage/RouteCreatePage";

const App: React.FC = () => {
  return (
    <Router>
      <Navbar />
      <div className="container">
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route
            path="/add-transaction"
            element={
              <PrivateRoute>
                <RouteSearchPage />
              </PrivateRoute>
            }
          />
          <Route
            path="/transactions"
            element={
              <PrivateRoute>
                <TransactionList />
              </PrivateRoute>
            }
          />
            <Route
                path="/my-tickets"
                element={
                    <PrivateRoute>
                        <TicketsPage />
                    </PrivateRoute>
                }
            />
            <Route
                path="/deposit"
                element={
                <PrivateRoute>
                    <DepositPage />
                </PrivateRoute>
                }
            />
          <Route
            path="/employee-schedule"
            element={
              <EmployeeRoute>
                <EmployeeSchedulePage />
              </EmployeeRoute>
            }
          />
          <Route
            path="/admin/users"
            element={
              <AdminRoute>
                <AdminUserRolesPage />
              </AdminRoute>
            }
          />
          <Route
            path="/admin/schedules"
            element={
              <AdminRoute>
                <AdminSchedulesPage />
              </AdminRoute>
            }
          />
          <Route
            path="/routes/new"
            element={
              <StaffRoute>
                <RouteCreatePage />
              </StaffRoute>
            }
          />
          <Route path="/test" element={<TestApiComponent />} />
          <Route
            path="/register"
            element={
              <PublicRoute>
                <RegisterPage />
              </PublicRoute>
            }
          />
          <Route
            path="/login"
            element={
              <PublicRoute>
                <LoginPage />
              </PublicRoute>
            }
          />
        </Routes>
      </div>
      <ToastContainer />
    </Router>
  );
};

export default App;
