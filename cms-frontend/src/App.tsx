import {
	Navigate,
	Route,
	BrowserRouter as Router,
	Routes,
} from "react-router-dom";
import "./App.css";
import { useAuth } from "./contexts/AuthContext";
import ChangePasswordPage from "./pages/ChangePasswordPage";
import ContactsPage from "./pages/ContactsPage";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";

function App() {
	const { isAuthenticated, isLoading } = useAuth();

	if (isLoading) {
		return (
			<div
				style={{
					display: "flex",
					justifyContent: "center",
					alignItems: "center",
					height: "100vh",
				}}
			>
				Loading...
			</div>
		);
	}

	return (
		<Router>
			<div className="app-container">
				<Routes>

					<Route
						path="/login"
						element={
							isAuthenticated ? (
								<Navigate
									to="/contacts"
									replace
								/>
							) : (
								<LoginPage />
							)
						}
					/>
					<Route
						path="/register"
						element={
							isAuthenticated ? (
								<Navigate
									to="/contacts"
									replace
								/>
							) : (
								<RegisterPage />
							)
						}
					/>


					<Route
						path="/contacts"
						element={
							isAuthenticated ? (
								<ContactsPage />
							) : (
								<Navigate
									to="/login"
									replace
								/>
							)
						}
					/>

					<Route
						path="/change-password"
						element={
							isAuthenticated ? (
								<ChangePasswordPage />
							) : (
								<Navigate
									to="/login"
									replace
								/>
							)
						}
					/>
					<Route
						path="/"
						element={
							isAuthenticated ? (
								<Navigate
									to="/contacts"
									replace
								/>
							) : (
								<Navigate
									to="/login"
									replace
								/>
							)
						}
					/>
				</Routes>
			</div>
		</Router>
	);
}

export default App;
