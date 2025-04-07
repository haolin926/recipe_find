import React, {useContext, useEffect, useState} from "react";
import {Avatar, Button, Card, Form, Input, message, Result, Spin, Upload} from "antd";
import {UploadOutlined, UserOutlined} from "@ant-design/icons";
import {AuthContext} from "../AuthContext";
import {Dialog, DialogTitle} from "@mui/material";
import "./ProfileComponent.css";
import Box from "@mui/material/Box";

function ProfileComponent () {
    const { user, updateUser, updatePassword, loading } = useContext(AuthContext); // Get user & updateUser function
    const [isDialogOpen, setIsDialogOpen] = useState(false);
    const [formData, setFormData] = useState({
        email: "",
        userPhoto: "",
        password: "",
        newPassword: "",
        confirmPassword: "",
        userPhotoPreview: "",
    });

    useEffect(() => {
        if (user) {
            setFormData({
                username: user.username || "",
                email: user.email || "",
                userPhoto: user.userPhoto || "",
                password: "",
                newPassword: "",
                confirmPassword: "",
                userPhotoPreview: "",
            });
        }
    }, [user]);

    if (loading) {
        return <Spin size="large" style={{ display: "block", margin: "auto" }} />;
    }

    if (!user) {
        return <Result
            status="403"
            title="Unauthorized"
            subTitle="You need to log in to access your profile."
        />;
    }

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const handleAvatarChange = (info) => {
        const file = info.file;

        if (!file) return;

        // Generate preview URL
        const previewUrl = URL.createObjectURL(file);

        // Read file as Base64
        const reader = new FileReader();
        reader.readAsDataURL(file);

        reader.onload = () => {
            setFormData({
                ...formData,
                userPhoto: reader.result, // Store Base64 data
                userPhotoPreview: previewUrl, // Store preview URL
            });
        };

        reader.onerror = (error) => {
            message.error("Failed to read image file");
            console.error("File reading error:", error);
        };
    };

    const handleProfileUpdate = async () => {
        try {
            const result = await updateUser(formData); // Ensure it completes before proceeding
            if (result === true) {
                message.success("Profile updated successfully!");
            } else {
                message.error("Failed to update profile");
            }
        } catch (error) {
            message.error("Failed to update profile. Please try again.");
            console.error("Profile update error:", error);
        }
    };

    const handlePasswordChange = async () => {
        if (formData.newPassword !== formData.confirmPassword) {
            message.error("New password and confirmation do not match!");
            return;
        }
        try {
            const result = await updatePassword(formData);
            if (result) {
                closeDialog();
            }
        } catch (error) {
            console.error("Failed to update password." + error);
        }
    };

    const openDialog = () => {
        setIsDialogOpen(true);
    };

    const closeDialog = () => {
        setIsDialogOpen(false);
    };


    return (
        <div style={{ display: "flex", justifyContent: "center", padding: 20 }}>
            <Card title="Profile Settings" style={{ width: 400, textAlign: "center" }}>
                {/* Profile Picture Upload */}
                <Upload
                    showUploadList={false}
                    beforeUpload={() => false} // Prevent automatic upload
                    onChange={handleAvatarChange} // Ensure onChange fires
                    accept="image/*" // Only allow images
                >
                    <Avatar
                        size={100}
                        src={formData.userPhotoPreview || formData.userPhoto}
                        icon={!formData.userPhoto && !formData.userPhotoPreview && <UserOutlined />}
                        style={{ cursor: "pointer", marginBottom: 10 }}
                    />
                    <br />
                    <Button icon={<UploadOutlined />}>Upload Profile Photo</Button>
                </Upload>
                <Form layout="vertical" className={"registerForm"}>

                    <Form.Item label="Email">
                        <Input name="email" type="email" value={formData.email} onChange={handleInputChange} />
                    </Form.Item>

                    <Button type="primary" block onClick={handleProfileUpdate}>
                        Save Profile
                    </Button>
                </Form>

                <Button className={"openDialogButton"} type="primary" block onClick={openDialog}>
                    Change Password
                </Button>
            </Card>

            <Dialog className={"passwordDialog"} open={isDialogOpen} onClose={closeDialog}>
                <DialogTitle>Change Password</DialogTitle>
                {/* Password Change */}
                <Card>
                    <Form className={"changePasswordForm"} layout="vertical" rules={
                            {
                                required: true,
                                message: 'Please input your password!',
                            }}>
                        <Form.Item label="Current Password">
                            <Input.Password name="password" value={formData.password} onChange={handleInputChange} />
                        </Form.Item>

                        <Form.Item label="New Password" name="newPassword" rules={[
                            {
                                required: true,
                                message: 'Please input your password!',
                            },
                            {
                                pattern: /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,20}$/,
                                message: 'Password must be 8-20 characters long and include at least one letter and one number!',
                            },
                        ]}>
                            <Input.Password name="newPassword" value={formData.newPassword} onChange={handleInputChange} />
                        </Form.Item>

                        <Form.Item label="Confirm New Password" name="confirmPassword" rules={[
                            {
                                required: true,
                                message: 'Please input your password again!',
                            },
                            ({ getFieldValue }) => ({
                                validator(_, value) {
                                    if (!value || getFieldValue('newPassword') === value) {
                                        return Promise.resolve();
                                    }
                                    return Promise.reject(new Error('The new password that you entered do not match!'));
                                },
                            }),
                        ]}>
                            <Input.Password name="confirmPassword" value={formData.confirmPassword} onChange={handleInputChange} />
                        </Form.Item>

                        <Box className={"buttonGroup"}>
                            <Button type="primary" danger block onClick={handlePasswordChange}>
                                Update Password
                            </Button>
                            <Button type={"primary"} block onClick={closeDialog}>
                                Cancel
                            </Button>
                        </Box>
                    </Form>
                </Card>
            </Dialog>
        </div>
    );
}
export default ProfileComponent;