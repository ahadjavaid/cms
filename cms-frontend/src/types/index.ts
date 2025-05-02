// User data structure based on backend DTO
export type User = {
	id: number;
	name: string;
	email: string;
	phoneNumber: string;
};

// Structure of the response from login/register endpoints
export type AuthResponse = {
	token: string;
	user: User;
};

// Structure for login credentials
export type LoginCredentials = {
	emailOrPhone: string;
	password: string;
};

// Structure for registration data
export type RegisterData = {
	name: string;
	email: string;
	phoneNumber: string;
	password: string;
};

export type ChangePasswordData = {
	currentPassword: string;
	newPassword: string;
};

export type UserDto = User;

// Structure for a single contact (matches backend ContactDto)
export type Contact = {
	id: number;
	userId: number; // Keep if needed, though often implicit from logged-in user
	firstName: string;
	lastName: string | null; // Match backend (can be null)
	email: string;
	phoneNumber: string | null; // Match backend (can be null)
};

// Structure for creating a contact (matches backend ContactCreateDto)
export type ContactCreateData = {
	firstName: string;
	lastName?: string; // Optional field
	email: string;
	phoneNumber?: string; // Optional field
};

// Structure for updating a contact (matches backend ContactUpdateDto)
export type ContactUpdateData = {
	firstName: string;
	lastName?: string;
	email: string;
	phoneNumber?: string;
};
