import { createBrowserRouter, RouterProvider } from "react-router-dom"
import { LoginPage } from "../pages/login/LoginPage"
import { TwoFactorPage } from "../pages/login/TwoFactorPage"
import { RegisterPage } from "../pages/register/RegisterPage"
import { DashboardPage } from "../pages/dashboard/DashboardPage"
import { AccountsPage } from "../pages/accounts/AccountsPage"
import { UsersPage } from "../pages/users/UsersPage"
import { CardsPage } from "../pages/cards/CardsPage"
import { TransactionsPage } from "../pages/transactions/TransactionsPage"
import { NewTransactionPage } from "../pages/transactions/NewTransactionPage"
import { AuditingPage } from "../pages/auditing/AuditingPage"
import { NotFoundPage } from "../pages/NotFoundPage"
import { PrivateRoute } from "./PrivateRoute"
import { AdminRoute } from "./AdminRoute"
import { AppLayout } from "../components/layout/AppLayout"

const router = createBrowserRouter([
  { path: "/login", element: <LoginPage /> },
  { path: "/login/2fa", element: <TwoFactorPage /> },
  { path: "/register", element: <RegisterPage /> },
  {
    element: <PrivateRoute />,
    children: [
      {
        element: <AppLayout />,
        children: [
          { path: "/dashboard", element: <DashboardPage /> },
          { path: "/accounts", element: <AccountsPage /> },
          { path: "/cards", element: <CardsPage /> },
          { path: "/transactions", element: <TransactionsPage /> },
          { path: "/transactions/new", element: <NewTransactionPage /> },
        ],
      },
    ],
  },
  {
    element: <AdminRoute />,
    children: [
      {
        element: <AppLayout />,
        children: [
          { path: "/users", element: <UsersPage /> },
          { path: "/auditing", element: <AuditingPage /> },
        ],
      },
    ],
  },
  { path: "*", element: <NotFoundPage /> },
])

export function AppRouter() {
  return <RouterProvider router={router} />
}
