import React, { useState, FormEvent } from "react";
import { useAuth } from "../contexts/AuthContext";
import { RegisterData } from "../types";
import { Link } from "react-router-dom";
import "../styles/FormStyles.css";

const RegisterPage: React.FC = () => {
	const [name, setName] = useState("");
	const [email, setEmail] = useState("");
	const [phoneNumber, setPhoneNumber] = useState("");
	const [password, setPassword] = useState("");
	const [confirmPassword, setConfirmPassword] = useState("");
	const [error, setError] = useState<string | null>(null);
	const [isLoading, setIsLoading] = useState(false);
	const { register } = useAuth();

	const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
		e.preventDefault();
		setError(null);
		if (password !== confirmPassword) {
			setError("Passwords do not match.");
			return;
		}
		if (password.length < 8) {
			setError("Password must be at least 8 characters long.");
			return;
		}

		setIsLoading(true);
		const data: RegisterData = { name, email, phoneNumber, password };

		try {
			await register(data);
		} catch (err: any) {
			const backendError =
				err.response?.data?.message ||
				err.message ||
				"Registration failed. Please try again.";
			setError(backendError);
			console.error("Registration error:", err);
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
				<h2>Register</h2>

				{error && <p className="error-message">{error}</p>}

				<div className="form-group">
					<label htmlFor="name">Name</label>
					<input
						type="text"
						id="name"
						value={name}
						onChange={(e) => setName(e.target.value)}
						required
						disabled={isLoading}
						placeholder="Enter your full name"
					/>
				</div>

				<div className="form-group">
					<label htmlFor="email">Email</label>
					<input
						type="email"
						id="email"
						value={email}
						onChange={(e) => setEmail(e.target.value)}
						required
						disabled={isLoading}
						placeholder="Enter your email address"
					/>
				</div>

				<div className="form-group">
					<label htmlFor="phoneNumber">Phone Number</label>
					<input
						type="tel"
						id="phoneNumber"
						value={phoneNumber}
						onChange={(e) => setPhoneNumber(e.target.value)}
						required
						disabled={isLoading}
						placeholder="e.g., +1234567890"
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
						minLength={8}
						disabled={isLoading}
						placeholder="Create a password (min 8 characters)"
					/>
				</div>

				<div className="form-group">
					<label htmlFor="confirmPassword">Confirm Password</label>
					<input
						type="password"
						id="confirmPassword"
						value={confirmPassword}
						onChange={(e) => setConfirmPassword(e.target.value)}
						required
						minLength={8}
						disabled={isLoading}
						placeholder="Confirm your password"
					/>
				</div>

				<button
					type="submit"
					className="button-primary"
					disabled={isLoading}
				>
					{isLoading ? "Registering..." : "Register"}
				</button>

				<div className="form-links">
					Already have an account?
					<Link to="/login">Login here</Link>
				</div>
			</form>
		</div>
	);
};

export default RegisterPage;
