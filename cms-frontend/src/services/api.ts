import axios, { AxiosError, InternalAxiosRequestConfig } from "axios";

const API_BASE_URL: string = "http://localhost:8080";

const apiClient = axios.create({
	baseURL: API_BASE_URL,
	headers: {
		"Content-Type": "application/json",
	},
});

apiClient.interceptors.request.use(
	(config: InternalAxiosRequestConfig) => {
		const token = localStorage.getItem("jwtToken");
		if (token) {
			config.headers.Authorization = `Bearer ${token}`;
		}
		return config;
	},
	(error: AxiosError) => {
		console.error("Request error:", error);
		return Promise.reject(error);
	}
);

export default apiClient;
