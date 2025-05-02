import React, { useState, FormEvent } from "react";
import { useAuth } from "../contexts/AuthContext";
import { ChangePasswordData } from "../types";
import { Link } from "react-router-dom";
import "../styles/FormStyles.css";

const ChangePasswordPage: React.FC = () => {
	const [currentPassword, setCurrentPassword] = useState("");
	const [newPassword, setNewPassword] = useState("");
	const [confirmPassword, setConfirmPassword] = useState("");
	const [error, setError] = useState<string | null>(null);
	const [success, setSuccess] = useState<string | null>(null);
	const [isLoading, setIsLoading] = useState(false);
	const { changePassword } = useAuth();

	const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
		e.preventDefault();
		setError(null);
		setSuccess(null);

		if (newPassword !== confirmPassword) {
			setError("New passwords do not match.");
			return;
		}

		if (newPassword.length < 8) {
			setError("New password must be at least 8 characters long.");
			return;
		}

		setIsLoading(true);
		const data: ChangePasswordData = { currentPassword, newPassword };

		try {
			await changePassword(data);
			setSuccess("Password changed successfully!");
			setCurrentPassword("");
			setNewPassword("");
			setConfirmPassword("");
		} catch (err: any) {
			const backendError =
				err.response?.data?.message ||
				err.message ||
				"Failed to change password.";
			setError(backendError);
			console.error("Password change error:", err);
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
				<h2>Change Password</h2>

				{error && <p className="error-message">{error}</p>}
				{success && <p className="success-message">{success}</p>}

				<div className="form-group">
					<label htmlFor="currentPassword">Current Password</label>
					<input
						type="password"
						id="currentPassword"
						value={currentPassword}
						onChange={(e) => setCurrentPassword(e.target.value)}
						required
						disabled={isLoading}
					/>
				</div>

				<div className="form-group">
					<label htmlFor="newPassword">New Password</label>
					<input
						type="password"
						id="newPassword"
						value={newPassword}
						onChange={(e) => setNewPassword(e.target.value)}
						required
						minLength={8}
						disabled={isLoading}
					/>
				</div>

				<div className="form-group">
					<label htmlFor="confirmPassword">
						Confirm New Password
					</label>
					<input
						type="password"
						id="confirmPassword"
						value={confirmPassword}
						onChange={(e) => setConfirmPassword(e.target.value)}
						required
						minLength={8}
						disabled={isLoading}
					/>
				</div>

				<button
					type="submit"
					className="button-primary"
					disabled={isLoading}
				>
					{isLoading ? "Changing..." : "Change Password"}
				</button>

				<div className="form-links">
					<Link to="/contacts">Back to Contacts</Link>
				</div>
			</form>
		</div>
	);
};

export default ChangePasswordPage;
