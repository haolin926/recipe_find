import * as React from 'react';
import AppBar from '@mui/material/AppBar';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import {Button, IconButton} from "@mui/material";
import { MenuOutlined, UserOutlined} from "@ant-design/icons";
import Box from "@mui/material/Box";
import { useNavigate } from 'react-router-dom';
import {AuthContext} from "../AuthContext";
import {Dropdown, message} from "antd";

function ResponsiveAppBar({toggleDrawer}) {
    const {user, logout} = React.useContext(AuthContext);
    const navigate = useNavigate();

    const handleLogout = async () => {
        const result = await logout();

        if (result.success) {
            message.success(result.message);
            navigate("/");
        } else {
            message.error(result.message);
        }
    }

    const items = [
        {
            key: '1',
            label: (
                <Typography>Profile</Typography>
            ),
        },
        {
            key: '2',
            label: (
                <Typography onClick={handleLogout}>Log Out</Typography>
            ),
        }
    ];

    const handleRegisterClick = () => {
        navigate("/signup");
    };

    const handleLoginClick = () => {
        navigate("/login");
    };

    return (
        <Box sx={{maxWidth: "100vw"}}>
            <AppBar position="fixed" sx={{backgroundColor:"#e67e22", color:"black", maxWidth:"100%"}}>
                <Box>
                    <Toolbar disableGutters>
                        <IconButton onClick={toggleDrawer} color={"primary"}>
                            <MenuOutlined style={{color:"black"}}/>
                        </IconButton>
                        <Typography variant="h6" component="div" sx={{flexGrow: "1", color:"white" }}>
                            Recipe Search
                        </Typography>
                        <Box sx={{marginRight:"10px"}}>
                        {user != null ? (
                            <>
                                <Dropdown menu={{items}} trigger={["click"]} overlayStyle={{ zIndex: 1301 }}>
                                    <IconButton>
                                        <UserOutlined style={{color:"white"}}/>
                                    </IconButton>
                                </Dropdown>
                            </>
                            ):(
                                <>
                                    <Button color="inherit" sx={{color:"white"}} onClick={handleRegisterClick}>Sign up</Button>
                                    <Button color="inherit" sx={{color:"white"}} onClick={handleLoginClick}>Login</Button>
                                </>
                            )}
                        </Box>
                    </Toolbar>
                </Box>
            </AppBar>
        </Box>
    );
}
export default ResponsiveAppBar;
