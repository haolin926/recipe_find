import * as React from 'react';
import AppBar from '@mui/material/AppBar';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';

import Button from '@mui/material/Button';
import {IconButton} from "@mui/material";
import {MenuOutlined} from "@ant-design/icons";
import Box from "@mui/material/Box";

function ResponsiveAppBar({toggleDrawer}) {
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
                        <Button color="inherit" sx={{color:"white"}}>Sign up</Button>
                        <Button color="inherit" sx={{color:"white"}}>Login</Button>
                    </Toolbar>
                </Box>
            </AppBar>
        </Box>
    );
}
export default ResponsiveAppBar;
