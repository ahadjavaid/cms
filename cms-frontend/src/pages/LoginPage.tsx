import React, { useState, FormEvent } from "react";
import { useAuth } from "../contexts/AuthContext";
import { LoginCredentials } from "../types";
import { Link } from "react-router-dom";
import "../styles/FormStyles.css";

const LoginPage: React.FC = () => {
	const [emailOrPhone, setEmailOrPhone] = useState("");
	const [password, setPassword] = useState("");
	const [error, setError] = useState<string | null>(null);
	const [isLoading, setIsLoading] = useState(false);
	const { login } = useAuth();

	const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
		e.preventDefault();
		setError(null);
		setIsLoading(true);

		const credentials: LoginCredentials = { emailOrPhone, password };

		try {
			await login(credentials);
		} catch (err: any) {
			const backendError =
				err.response?.data?.message ||
				err.message ||
				"Login failed. Please check your credentials.";
			setError(backendError);
			console.error("Login error:", err);
		} finally {
			setIsLoading(false);
		}
	};

	return (
		<div className="form-container">
			<form
				onSubmit={handleSubmit}
				className="form-card"
			>
				<h2>Login</h2>

				{error && <p className="error-message">{error}</p>}

				<div className="form-group">
					<label htmlFor="emailOrPhone">Email or Phone Number</label>
					<input
						type="text"
						id="emailOrPhone"
						value={emailOrPhone}
						onChange={(e) => setEmailOrPhone(e.target.value)}
						required
						disabled={isLoading}
						placeholder="Enter your email or phone"
					/>
				</div>

				<div className="form-group">
					<label htmlFor="password">Password</label>
					<input
						type="password"
						id="password"
						value={password}
						onChange={(e) => setPassword(e.target.value)}
						required
						disabled={isLoading}
						placeholder="Enter your password"
					/>
				</div>

				<button
					type="submit"
					className="button-primary"
					disabled={isLoading}
				>
					{isLoading ? "Logging in..." : "Login"}
				</button>

				<div className="form-links">
					Don't have an account?
					<Link to="/register">Register here</Link>
				</div>
			</form>
		</div>
	);
};

export default LoginPage;
