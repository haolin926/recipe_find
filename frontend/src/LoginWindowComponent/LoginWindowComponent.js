import React, {useContext, useEffect} from "react";
import {AuthContext} from "../AuthContext";
import {useNavigate} from "react-router-dom";
import {Button, Form, Input, message} from "antd";
import {Link, Paper} from "@mui/material";
import AppBar from "@mui/material/AppBar";
import {LockOutlined, UserOutlined} from "@ant-design/icons";

function LoginWindowComponent () {
    const { user, login } = useContext(AuthContext);
    const navigate = useNavigate();

    useEffect(() => {
        if (user) {
            navigate("/");
        }
    }, [user, navigate]);

    const onFinish = async (values) => {
        const result = await login(values);

        if (result.success) {
            message.success("Login successful");
            navigate("/");
        } else {
            message.error(result.message);
        }
    };

    const onFinishFailed = (errorInfo) => {
        message.error("Login failed, Please try again");
    };

    return (
        <Paper sx={{display:"flex", flexDirection:"column", alignItems:"center", justifyContent:"center", width:"100%", height:"50%"}}>
            <AppBar className={"commonHeader"} position={"static"}>
                <h2>Sign In</h2>
            </AppBar>
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
                    ]}
                >
                    <Input prefix={<UserOutlined />}/>
                </Form.Item>

                <Form.Item
                    label="Password"
                    name="password"
                    rules={[
                        {
                            required: true,
                            message: 'Please input your password!',
                        },
                    ]}
                >
                    <Input.Password  prefix={<LockOutlined/>}/>
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
                <Link href="signup">Haven't got an account yet? Click here to create one</Link>
            </Form>
        </Paper>
    );
}

export default LoginWindowComponent;