import React, { useState, useEffect, useCallback } from "react";
import { useAuth } from "../contexts/AuthContext";
import { Contact, ContactCreateData, ContactUpdateData } from "../types";
import * as contactService from "../services/contactService";
import { Link } from "react-router-dom";
import Modal from "../components/Modal";
import ContactForm from "../components/ContactForm";
import "../styles/ContactsPage.css";

const ContactsPage: React.FC = () => {
	const { user, logout } = useAuth();
	const [contacts, setContacts] = useState<Contact[]>([]);
	const [isLoading, setIsLoading] = useState(false);
	const [isSubmitting, setIsSubmitting] = useState(false);
	const [error, setError] = useState<string | null>(null);
	const [searchTerm, setSearchTerm] = useState("");

	const [isAddModalOpen, setIsAddModalOpen] = useState(false);
	const [isEditModalOpen, setIsEditModalOpen] = useState(false);
	const [editingContact, setEditingContact] = useState<Contact | null>(null);

	const fetchContacts = useCallback(async () => {
		setIsLoading(true);
		setError(null);
		try {
			let fetchedContacts;
			if (searchTerm.trim()) {
				fetchedContacts = await contactService.searchContacts(
					searchTerm
				);
			} else {
				fetchedContacts = await contactService.getAllContacts();
			}
			setContacts(fetchedContacts);
		} catch (err: any) {
			setError(
				err.response?.data?.message ||
					err.message ||
					"Failed to load contacts."
			);
		} finally {
			setIsLoading(false);
		}
	}, [searchTerm]);

	useEffect(() => {
		fetchContacts();
	}, [fetchContacts]);

	const openAddModal = () => setIsAddModalOpen(true);
	const closeAddModal = () => setIsAddModalOpen(false);

	const openEditModal = (contact: Contact) => {
		setEditingContact(contact);
		setIsEditModalOpen(true);
	};
	const closeEditModal = () => {
		setEditingContact(null);
		setIsEditModalOpen(false);
	};

	const handleAddContact = async (
		data: ContactCreateData | ContactUpdateData
	) => {
		setIsSubmitting(true);
		try {
			await contactService.createContact(data as ContactCreateData);
			closeAddModal();
			fetchContacts();
		} catch (error) {
			console.error("Add contact failed:", error);
			throw error;
		} finally {
			setIsSubmitting(false);
		}
	};

	const handleUpdateContact = async (
		data: ContactCreateData | ContactUpdateData
	) => {
		if (!editingContact) return;
		setIsSubmitting(true);
		try {
			await contactService.updateContact(
				editingContact.id,
				data as ContactUpdateData
			);
			closeEditModal();
			fetchContacts();
		} catch (error) {
			console.error("Update contact failed:", error);
			throw error;
		} finally {
			setIsSubmitting(false);
		}
	};

	const handleDelete = async (contactId: number) => {
		if (window.confirm("Are you sure you want to delete this contact?")) {
			setIsLoading(true);
			try {
				await contactService.deleteContact(contactId);
				fetchContacts();
			} catch (err: any) {
				setError(
					err.response?.data?.message ||
						err.message ||
						"Failed to delete contact."
				);
				setIsLoading(false);
			}

		}
	};

	const handleSearchChange = (event: React.ChangeEvent<HTMLInputElement>) => {
		setSearchTerm(event.target.value);
	};

	const handleSearchSubmit = (event?: React.FormEvent<HTMLFormElement>) => {
		event?.preventDefault();
		fetchContacts();
	};

	return (
		<div className="contacts-page-container">
			{/* Header */}
			<header className="contacts-header">
				<h1>Welcome, {user?.name || "User"}!</h1>
				<nav>

					<button
						className="button-add-contact"
						onClick={openAddModal}
					>
						+ Add Contact
					</button>
					<Link
						to="/change-password"
						className="button-secondary-link"
					>
						Change Password
					</Link>
					<button
						onClick={logout}
						className="button-logout"
					>
						Logout
					</button>
				</nav>
			</header>


			<div className="contacts-content">
				<h2>Your Contacts</h2>
				<form
					onSubmit={handleSearchSubmit}
					className="search-bar"
				>
					<input
						type="text"
						placeholder="Search by name, email, or phone..."
						value={searchTerm}
						onChange={handleSearchChange}
						className="search-input"
					/>
					<button
						type="submit"
						className="button-search"
						disabled={isLoading}
					>
						{isLoading ? "Searching..." : "Search"}
					</button>
				</form>

				{isLoading && (
					<p className="loading-message">Loading contacts...</p>
				)}
				{error && <p className="error-message">{error}</p>}
				{!isLoading && !error && (
					<div className="contacts-list">
						{contacts.length > 0 ? (
							contacts.map((contact) => (
								<div
									key={contact.id}
									className="contact-item"
								>
									<div className="contact-info">
										<span className="contact-name">
											{contact.firstName}{" "}
											{contact.lastName || ""}
										</span>
										<span className="contact-detail">
											{contact.email}
										</span>
										{contact.phoneNumber && (
											<span className="contact-detail">
												{contact.phoneNumber}
											</span>
										)}
									</div>
									<div className="contact-actions">
										<button
											className="button-edit"
											onClick={() =>
												openEditModal(contact)
											}
										>
											Edit
										</button>
										<button
											className="button-delete"
											onClick={() =>
												handleDelete(contact.id)
											}
											disabled={isLoading}
										>
											Delete
										</button>
									</div>
								</div>
							))
						) : (
							<p className="no-contacts-message">
								{searchTerm
									? "No contacts found matching your search."
									: "You have no contacts yet. Add one!"}
							</p>
						)}
					</div>
				)}
			</div>


			<Modal
				isOpen={isAddModalOpen}
				onClose={closeAddModal}
				title="Add New Contact"
			>
				<ContactForm
					onSubmit={handleAddContact}
					onCancel={closeAddModal}
					isSubmitting={isSubmitting}
				/>
			</Modal>


			<Modal
				isOpen={isEditModalOpen}
				onClose={closeEditModal}
				title="Edit Contact"
			>
				<ContactForm
					initialContact={editingContact}
					onSubmit={handleUpdateContact}
					onCancel={closeEditModal}
					isSubmitting={isSubmitting}
				/>
			</Modal>
		</div>
	);
};

export default ContactsPage;
