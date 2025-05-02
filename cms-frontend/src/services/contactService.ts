import apiClient from "./api";
import { Contact, ContactCreateData, ContactUpdateData } from "../types";

const BASE_URL = "/contacts";
export const getAllContacts = async (): Promise<Contact[]> => {
	try {
		const response = await apiClient.get<Contact[]>(BASE_URL);
		return response.data;
	} catch (error: any) {
		console.error(
			"Error fetching contacts:",
			error.response?.data || error.message
		);
		throw error;
	}
};

export const searchContacts = async (query: string): Promise<Contact[]> => {
	if (!query.trim()) {
		return getAllContacts();
	}
	try {
		const response = await apiClient.get<Contact[]>(`${BASE_URL}/search`, {
			params: { query },
		});
		return response.data;
	} catch (error: any) {
		console.error(
			"Error searching contacts:",
			error.response?.data || error.message
		);
		throw error;
	}
};

export const createContact = async (
	contactData: ContactCreateData
): Promise<Contact> => {
	try {
		const response = await apiClient.post<Contact>(BASE_URL, contactData);
		return response.data;
	} catch (error: any) {
		console.error(
			"Error creating contact:",
			error.response?.data || error.message
		);
		throw error;
	}
};

export const updateContact = async (
	contactId: number,
	contactData: ContactUpdateData
): Promise<Contact> => {
	try {
		const response = await apiClient.put<Contact>(
			`${BASE_URL}/${contactId}`,
			contactData
		);
		return response.data;
	} catch (error: any) {
		console.error(
			`Error updating contact ${contactId}:`,
			error.response?.data || error.message
		);
		throw error;
	}
};

export const deleteContact = async (contactId: number): Promise<void> => {
	try {
		await apiClient.delete<void>(`${BASE_URL}/${contactId}`);
	} catch (error: any) {
		console.error(
			`Error deleting contact ${contactId}:`,
			error.response?.data || error.message
		);
		throw error;
	}
};
