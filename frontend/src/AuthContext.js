import {createContext, useEffect, useState} from "react";
import axios from "axios";
import {message} from "antd";

export const AuthContext = createContext();

export const AuthProvider = ({children}) => {
    const [user, setUser] = useState(null);

    useEffect (() => {
        axios.get("http://localhost:8080/api/user/info", {withCredentials: true})
            .then(res => setUser(res.data))
            .catch((error) => {
                console.error("Error fetching user:", error);
                setUser(null);
            });
    }, [])
    const login = async (values) => {
        try {
            const response = await axios.post("http://localhost:8080/api/user/login", {
                username: values.username,
                password: values.password
            }, { withCredentials: true });

            if (response.status === 200) {
                setUser(response.data); // Store user data in context
                return { success: true };
            }
        } catch (error) {
            if (error.response.status === 401) {
                return { success: false, message: "Invalid Username/Password" };
            } else {
                return { success: false, message: "Server error, unable to login" };
            }
        }
    };
    const logout = async () => {
        try {
            const response = await axios.post("http://localhost:8080/api/user/logout",
                {}, { withCredentials: true });

            if (response.status === 200) {
                setUser(null);
                return { success: true, message: "Logout successful" };
            } else {
                return { success: false, message: "Logout failed, please try again." };
            }
        } catch (error) {
            console.error("Logout error:", error);
            return { success: false, message: "Server error, unable to logout." };
        }
    };

    const register = async (values) => {
        try {
            const response = await axios.post("http://localhost:8080/api/user/register", {
                username: values.username,
                password: values.password,
                email: values.email
            });

            if (response.status === 200) {
                return { success: true, message: "Register successful, redirecting..." };
            } else {
                return { success: false, message: "Register failed, Please try again" };
            }
        } catch (error) {
            return { success: false, message: "Register failed, Please try again" };
        }
    };


    return (
        <AuthContext.Provider value={{user, login, logout, register}}>
            {children}
        </AuthContext.Provider>
    );
}
