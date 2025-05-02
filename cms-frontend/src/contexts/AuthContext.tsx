import React, {
	createContext,
	useState,
	useContext,
	useEffect,
	ReactNode,
} from "react";
import apiClient from "../services/api";
import {
	User,
	AuthResponse,
	LoginCredentials,
	RegisterData,
	ChangePasswordData,
	UserDto,
} from "../types";

interface AuthState {
	isAuthenticated: boolean;
	user: User | null;
	token: string | null;
	isLoading: boolean;
}

interface AuthContextType extends AuthState {
	login: (credentials: LoginCredentials) => Promise<void>;
	register: (data: RegisterData) => Promise<void>;
	logout: () => void;
	changePassword: (data: ChangePasswordData) => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

interface AuthProviderProps {
	children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
	const [authState, setAuthState] = useState<AuthState>({
		isAuthenticated: false,
		user: null,
		token: null,
		isLoading: true,
	});

	useEffect(() => {
		console.log("AuthProvider: Checking local storage for auth data...");
		try {
			const storedToken = localStorage.getItem("jwtToken");
			const storedUser = localStorage.getItem("user");

			if (storedToken && storedUser) {
				console.log(
					"AuthProvider: Found token and user in local storage."
				);
				const parsedUser: User = JSON.parse(storedUser);
				apiClient.defaults.headers.common[
					"Authorization"
				] = `Bearer ${storedToken}`;
				setAuthState({
					isAuthenticated: true,
					user: parsedUser,
					token: storedToken,
					isLoading: false,
				});
			} else {
				console.log(
					"AuthProvider: No auth data found in local storage."
				);
				setAuthState((prevState) => ({
					...prevState,
					isLoading: false,
				}));
			}
		} catch (error) {
			console.error(
				"AuthProvider: Error reading from local storage",
				error
			);
			localStorage.removeItem("jwtToken");
			localStorage.removeItem("user");
			setAuthState((prevState) => ({
				...prevState,
				isLoading: false,
			}));
		}
	}, []);

	const login = async (credentials: LoginCredentials) => {
		console.log("AuthProvider: Attempting login...");
		try {
			const response = await apiClient.post<AuthResponse>(
				"/user/login",
				credentials
			);
			const { token, user } = response.data;

			console.log("AuthProvider: Login successful.");
			localStorage.setItem("jwtToken", token);
			localStorage.setItem("user", JSON.stringify(user));
			apiClient.defaults.headers.common[
				"Authorization"
			] = `Bearer ${token}`;

			setAuthState({
				isAuthenticated: true,
				user: user,
				token: token,
				isLoading: false,
			});
		} catch (error: any) {
			console.error(
				"AuthProvider: Login failed.",
				error.response?.data || error.message
			);
			localStorage.removeItem("jwtToken");
			localStorage.removeItem("user");
			setAuthState({
				isAuthenticated: false,
				user: null,
				token: null,
				isLoading: false,
			});
			throw error;
		}
	};

	const register = async (data: RegisterData) => {
		console.log("AuthProvider: Attempting registration...");
		try {
			const response = await apiClient.post<AuthResponse>(
				"/user/signup",
				data
			);
			const { token, user } = response.data;

			console.log("AuthProvider: Registration successful.");
			localStorage.setItem("jwtToken", token);
			localStorage.setItem("user", JSON.stringify(user));
			apiClient.defaults.headers.common[
				"Authorization"
			] = `Bearer ${token}`;

			setAuthState({
				isAuthenticated: true,
				user: user,
				token: token,
				isLoading: false,
			});
		} catch (error: any) {
			console.error(
				"AuthProvider: Registration failed.",
				error.response?.data || error.message
			);
			setAuthState({
				isAuthenticated: false,
				user: null,
				token: null,
				isLoading: false,
			});
			throw error;
		}
	};

	const logout = () => {
		console.log("AuthProvider: Logging out.");
		localStorage.removeItem("jwtToken");
		localStorage.removeItem("user");
		delete apiClient.defaults.headers.common["Authorization"];

		setAuthState({
			isAuthenticated: false,
			user: null,
			token: null,
			isLoading: false,
		});
	};

	const changePassword = async (data: ChangePasswordData) => {
		console.log("AuthProvider: Attempting password change...");
		if (!authState.isAuthenticated) {
			throw new Error("User is not authenticated.");
		}
		try {
			await apiClient.post<UserDto>("/user/change-password", data);
			console.log("AuthProvider: Password change successful.");
		} catch (error: any) {
			console.error(
				"AuthProvider: Password change failed.",
				error.response?.data || error.message
			);
			throw error;
		}
	};

	const contextValue: AuthContextType = {
		...authState,
		login,
		register,
		logout,
		changePassword,
	};

	return (
		<AuthContext.Provider value={contextValue}>
			{children}
		</AuthContext.Provider>
	);
};

export const useAuth = (): AuthContextType => {
	const context = useContext(AuthContext);
	if (context === undefined) {
		throw new Error("useAuth must be used within an AuthProvider");
	}
	return context;
};
