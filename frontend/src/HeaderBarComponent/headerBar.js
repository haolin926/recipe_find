import * as React from 'react';
import AppBar from '@mui/material/AppBar';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import {Button, IconButton} from "@mui/material";
import { MenuOutlined, UserOutlined} from "@ant-design/icons";
import Box from "@mui/material/Box";
import {useNavigate} from 'react-router-dom';
import {AuthContext} from "../AuthContext";
import {Avatar, Dropdown, message} from "antd";
import PropTypes from "prop-types";


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

    const handleProfileClick = () => {
        navigate("/profile");
    }

    const items = [
        {
            key: '1',
            label: (
                <Typography onClick={handleProfileClick}>Profile</Typography>
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
            <AppBar id = "mainBar" position="fixed">
                <Box>
                    <Toolbar disableGutters>
                        <IconButton onClick={toggleDrawer} color={"primary"}>
                            <MenuOutlined style={{color:"black"}}/>
                        </IconButton>
                        <Box id={"headerLeftBox"}>
                            <Typography variant="h6" component="div" sx={{color:"white" }}>
                                Recipe Search
                            </Typography>
                        </Box>
                        <Box className={"headerButtonBox"}>
                        {user != null ? (
                            <Dropdown menu={{items}} trigger={["click"]} overlayStyle={{ zIndex: 1301 }}>
                                <Avatar
                                    src={user.userPhoto}
                                    icon={!user.userPhoto && <UserOutlined/>}
                                    style={{ cursor: "pointer", marginBottom: 10 }}
                                />
                            </Dropdown>
                            ):(
                                <>
                                    <Button color="inherit" onClick={handleRegisterClick}>Sign up</Button>
                                    <Button color="inherit" onClick={handleLoginClick}>Login</Button>
                                </>
                            )}
                        </Box>
                    </Toolbar>
                </Box>
            </AppBar>
        </Box>
    );
}

ResponsiveAppBar.propTypes = {
    toggleDrawer: PropTypes.func.isRequired,
};
export default ResponsiveAppBar;
