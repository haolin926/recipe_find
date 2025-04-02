import {createContext, useEffect, useMemo, useState} from "react";
import axios from "axios";
import {message} from "antd";
import PropTypes from "prop-types";

export const AuthContext = createContext();

export const AuthProvider = ({children}) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    useEffect (() => {
        axios.get("http://localhost:8080/api/user/info", {withCredentials: true})
            .then(res => {
                if (res.data === null || res.data === "") {
                    setUser(null);
                } else {
                    setUser(res.data);
                }
            })
            .catch(() => {
                setUser(null);
            })
            .finally(() => setLoading(false));
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
            if ((error.response.status !== undefined) && error.response.status === 401) {
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

    const updateUser = async (updatedData) => {
        try {
            const userDTO = {
                username: updatedData.username,
                email: updatedData.email,
                userPhoto: updatedData.userPhoto
            };

            const response = await axios.put("http://localhost:8080/api/user/update",
                userDTO,
                { withCredentials: true });
            if (response.status === 200) { // Check if update was successful
                setUser(response.data);
                return true;
            } // Update AuthContext with new user data
        } catch (error) {
            console.error("Failed to update profile", error);
            return false;
        }
    };


    const updatePassword = async (passwordData) => {
        const { password, newPassword } = passwordData;

        // Create the map or object to send to the backend
        const passwordMap = {
            "oldPassword": password,
            "newPassword": newPassword
        };
        try {
            const response = await axios.put("http://localhost:8080/api/user/changepassword",
                passwordMap,
                { withCredentials: true });
            if (response.status === 200) {
                message.success("Password updated successfully");
                return true;
            } else {
                message.error("Failed to update password");
                return false;
            }
        } catch (error) {
            if (error.response && error.response.status === 400) {
                message.error("The original password is incorrect");
            } else {
                message.error("Failed to update password");
            }
            console.error("Failed to update password", error);

        }
    };

    const authContextValue = useMemo(() => ({
        user,
        login,
        logout,
        register,
        loading,
        updateUser,
        updatePassword
    }), [user, loading]);

    return (
        <AuthContext.Provider value={authContextValue}>
            {children}
        </AuthContext.Provider>
    );
}
AuthProvider.propTypes = {
    children: PropTypes.node.isRequired
};
