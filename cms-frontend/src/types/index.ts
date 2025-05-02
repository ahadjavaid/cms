export type User = {
	id: number;
	name: string;
	email: string;
	phoneNumber: string;
};

export type AuthResponse = {
	token: string;
	user: User;
};

export type LoginCredentials = {
	emailOrPhone: string;
	password: string;
};

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

export type Contact = {
	id: number;
	userId: number;
	firstName: string;
	lastName: string | null;
	email: string;
	phoneNumber: string | null;
};

export type ContactCreateData = {
	firstName: string;
	lastName?: string;
	email: string;
	phoneNumber?: string;
};

export type ContactUpdateData = {
	firstName: string;
	lastName?: string;
	email: string;
	phoneNumber?: string;
};
