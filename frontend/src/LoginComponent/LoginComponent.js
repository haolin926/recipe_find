import Box from "@mui/material/Box";
import React, {useContext, useEffect} from "react";
import {AuthContext} from "../AuthContext";
import {useNavigate} from "react-router-dom";
import LoginWindowComponent from "../LoginWindowComponent/LoginWindowComponent";

function LoginComponent () {

    const { user} = useContext(AuthContext);
    const navigate = useNavigate();

    useEffect(() => {
        if (user) {
            navigate("/");
        }
    }, [user, navigate]);

    return (
        <Box className={"bodyBackground"} sx={{display:"flex", justifyContent:"center", lexDirection:"column"}}>
            <Box sx={{width:"40%", marginTop:"10%", height:"100%"}}>
                <LoginWindowComponent/>
            </Box>
        </Box>
    );
}
export default LoginComponent;