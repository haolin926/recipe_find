import Box from "@mui/material/Box";
import {Link, Paper} from "@mui/material";
import AppBar from "@mui/material/AppBar";
import {Avatar, Button, Form, Input, message, Upload} from "antd";
import React, {useContext, useState} from "react";
import {AuthContext} from "../AuthContext";
import "./SignUpComponent.css";
import {UploadOutlined, UserOutlined} from "@ant-design/icons";

function SignUpComponent () {

    const {register} = useContext(AuthContext);

    const [userPhoto, setUserPhoto] = useState(null);

    const handleAvatarChange = (info) => {
        const file = info.file;

        if (!file) return;

        // Read file as Base64
        const reader = new FileReader();
        reader.readAsDataURL(file);

        reader.onload = () => {
            setUserPhoto(reader.result);
        };

        reader.onerror = (error) => {
            message.error("Failed to read image file");
            console.error("File reading error:", error);
        };
    };

    const onFinish = async (values) => {
        let registerPhoto;

        if (!userPhoto) {
            registerPhoto = "";
        } else {
            registerPhoto = userPhoto;
        }
        const result = await register({...values, registerPhoto});

        if (result.success) {
            message.success(result.message);

            setTimeout(() => {
                if (window.history.length > 1) {
                    window.history.back();
                } else {
                    window.location.href = '/';
                }
            }, 3000);
        } else {
            message.error(result.message);
        }
    };

    const onFinishFailed = (errorInfo) => {
        message.error("Register failed, Please try again").then(r => console.log(r));
        console.log('Failed:', errorInfo);
    };

    return (
        <Box className={"bodyBackground"} sx={{display:"flex", justifyContent:"center", lexDirection:"column"}}>
            <Paper className={"signUpPaper"}>
                <AppBar className={"commonHeader"} position={"static"}>
                    <h2>Sign Up</h2>
                </AppBar>
                <Box className={"photoContainer"}>
                    <Upload
                        showUploadList={false}
                        beforeUpload={() => false} // Prevent automatic upload
                        onChange={handleAvatarChange} // Ensure onChange fires
                        accept="image/*" // Only allow images
                    >
                        <Avatar
                            className={"centeredAvatar"}
                            size={100}
                            src={userPhoto}
                            icon={!userPhoto && <UserOutlined />}
                            style={{ cursor: "pointer", marginBottom: 10 }}
                        />
                        <br />
                        <Button icon={<UploadOutlined />}>Upload Profile Photo</Button>
                    </Upload>
                </Box>
                <Form
                    name="basic"
                    labelCol={{
                        span: 7,
                    }}
                    wrapperCol={{
                        span: 14,
                    }}
                    initialValues={{
                        remember: true,
                    }}
                    onFinish={onFinish}
                    onFinishFailed={onFinishFailed}
                    autoComplete="off"
                    style={{padding: "20px", width:"100%"}}
                >
                    <Form.Item
                        label="Username"
                        name="username"
                        rules={[
                            {
                                required: true,
                                message: 'Please input your username!',
                            },
                            {
                                pattern: /^[A-Za-z0-9]{1,20}$/,
                                message: 'Username must be alphanumeric and no longer than 20 characters!',
                            },
                        ]}
                    >
                        <Input/>
                    </Form.Item>

                    <Form.Item
                        label="E-mail"
                        name="email"
                        rules={[
                            {
                                required: true,
                                message: 'Please input your email!',
                            },
                            {
                                type:"email",
                                message: 'The email is not valid!'
                            }
                        ]}
                    >
                        <Input/>
                    </Form.Item>

                    <Form.Item
                        label="Password"
                        name="password"
                        rules={[
                            {
                                required: true,
                                message: 'Please input your password!',
                            },
                            {
                                pattern: /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,20}$/,
                                message: 'Password must be 8-20 characters long and include at least one letter and one number!',
                            },
                        ]}
                    >
                        <Input.Password/>
                    </Form.Item>

                    <Form.Item
                        label="Confirm Password"
                        name="ConfirmPassword"
                        rules={[
                            {
                                required: true,
                                message: 'Please input your password again!',
                            },
                            ({ getFieldValue }) => ({
                                validator(_, value) {
                                    if (!value || getFieldValue('password') === value) {
                                        return Promise.resolve();
                                    }
                                    return Promise.reject(new Error('The new password that you entered do not match!'));
                                },
                            }),
                        ]}
                    >
                        <Input.Password/>
                    </Form.Item>

                    <Form.Item
                        label={null}
                        wrapperCol={{
                            span: 24,
                        }}
                        style={{ textAlign: "center" }}>
                        <Button type="primary" htmlType="submit">
                            Submit
                        </Button>
                    </Form.Item>
                    <Link href={"/login"}>Got account already? Click here to login</Link>
                </Form>
            </Paper>
        </Box>
    );
}
export default SignUpComponent;