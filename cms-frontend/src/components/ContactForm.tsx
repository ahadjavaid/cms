import React, { useState, useEffect, FormEvent } from "react";
import { Contact, ContactCreateData, ContactUpdateData } from "../types";
import "../styles/FormStyles.css";

interface ContactFormProps {

	initialContact?: Contact | null;

	onSubmit: (data: ContactCreateData | ContactUpdateData) => Promise<void>;

	onCancel: () => void;

	isSubmitting: boolean;
}

const ContactForm: React.FC<ContactFormProps> = ({
	initialContact,
	onSubmit,
	onCancel,
	isSubmitting,
}) => {
	const [firstName, setFirstName] = useState("");
	const [lastName, setLastName] = useState("");
	const [email, setEmail] = useState("");
	const [phoneNumber, setPhoneNumber] = useState("");
	const [error, setError] = useState<string | null>(null);

	const isEditMode = !!initialContact;
	useEffect(() => {
		if (isEditMode && initialContact) {
			setFirstName(initialContact.firstName || "");
			setLastName(initialContact.lastName || "");
			setEmail(initialContact.email || "");
			setPhoneNumber(initialContact.phoneNumber || "");
		} else {

			setFirstName("");
			setLastName("");
			setEmail("");
			setPhoneNumber("");
		}
		setError(null);
	}, [initialContact, isEditMode]);

	const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
		e.preventDefault();
		setError(null);

		if (!firstName.trim()) {
			setError("First name is required.");
			return;
		}
		if (!email.trim()) {
			setError("Email is required.");
			return;
		}

		const contactData: ContactCreateData | ContactUpdateData = {
			firstName,
			...(lastName.trim() && { lastName }),
			email,
			...(phoneNumber.trim() && { phoneNumber }),
		};

		try {
			await onSubmit(contactData);
		} catch (err: any) {
			const backendError =
				err.response?.data?.message ||
				err.message ||
				(isEditMode
					? "Failed to update contact."
					: "Failed to create contact.");
			setError(backendError);
			console.error("Contact form submission error:", err);
		}
	};

	return (

		<form onSubmit={handleSubmit}>
			{error && <p className="error-message">{error}</p>}

			<div className="form-group">
				<label htmlFor="firstName">First Name *</label>
				<input
					type="text"
					id="firstName"
					value={firstName}
					onChange={(e) => setFirstName(e.target.value)}
					required
					disabled={isSubmitting}
					placeholder="e.g., firstName"
				/>
			</div>

			<div className="form-group">
				<label htmlFor="lastName">Last Name</label>
				<input
					type="text"
					id="lastName"
					value={lastName}
					onChange={(e) => setLastName(e.target.value)}
					disabled={isSubmitting}
					placeholder="e.g., lastName"
				/>
			</div>

			<div className="form-group">
				<label htmlFor="email">Email *</label>
				<input
					type="email"
					id="email"
					value={email}
					onChange={(e) => setEmail(e.target.value)}
					required
					disabled={isSubmitting}
					placeholder="e.g., name@example.com"
				/>
			</div>

			<div className="form-group">
				<label htmlFor="phoneNumber">Phone Number</label>
				<input
					type="tel"
					id="phoneNumber"
					value={phoneNumber}
					onChange={(e) => setPhoneNumber(e.target.value)}
					disabled={isSubmitting}
					placeholder="e.g., +1234567890"
				/>
			</div>


			<div
				style={{
					display: "flex",
					justifyContent: "flex-end",
					gap: "0.5rem",
					marginTop: "1rem",
				}}
			>
				<button
					type="button"
					className="button-secondary-link"
					onClick={onCancel}
					disabled={isSubmitting}
					style={{ padding: "0.6rem 1.2rem" }}
				>
					Cancel
				</button>
				<button
					type="submit"
					className="button-primary"
					disabled={isSubmitting}
				>
					{isSubmitting
						? "Saving..."
						: isEditMode
						? "Save Changes"
						: "Create Contact"}
				</button>
			</div>
		</form>
	);
};

export default ContactForm;
