import React, {Component} from "react";
import {withRouter} from "../withRouter/withRouter";
import { Button } from 'antd';
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import "./HomeComponent.css";
class HomeComponent extends Component {
    constructor(props) {
        super(props);
        this.state = {
            image: null,
            previewURL: null,
            name: null,
            loading: false,
            dialogOpen: false,
            predictName: null,
        };
    }

    handleSearchByNameClick = () => {
        this.props.navigate("/search", {state: {value:"1"}});
    }

    handleSearchByImageClick = () => {
        this.props.navigate("/search", {state: {value:"2"}});
    }


    uploadImage = (event) => {
        const file = event.target.files[0];
        if (file) {
            const previewURL = URL.createObjectURL(file);
            const formData = new FormData();
            formData.append("image", file);
            this.setState({
                image: formData,
                previewURL: previewURL,
                loading: true
            });

            try {
                fetch("http://localhost:8080/api/recipe/image", {
                    method: "POST",
                    body: formData
                }).then(response => {
                    if (!response.ok) {
                        this.setState({loading: false});
                        throw new Error("Error uploading image");
                    }
                    return response.text();
                }).then(data => {
                    this.setState({
                        loading: false,
                        dialogOpen: true,
                        predictName: data
                    });
                }).catch(error => {
                    this.setState({loading: false});
                    console.error("Error uploading image: ", error);
                });
            } catch (error) {
                this.setState({loading: false});
                console.error("Error uploading image: ", error);
            }

        }
    }

    inputText = () => {
        const name = document.getElementById("nameUpload").value;
        if(name === "" || name === null) {
            alert("Please input a name");
            return
        }
        this.setState({
            name: name,
            loading: true
        });

        this.getRecipe(name).catch(() => {
            this.setState({loading: false});
        });
    }

    getRecipe = async (name) => {
        try {
            const response = await fetch(`http://localhost:8080/api/recipe/name?queryName=${encodeURIComponent(name)}`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                }
            });

            if (!response.ok) {
                this.setState({loading: false});
                throw new Error("Error fetching Data");
            }
            const data = await response.json();
            this.setState({loading: false});
            this.props.navigate("/result", {state: {data: data}});

        } catch (error) {
            console.error("Error fetching Data: ", error);
        }
    }

    closeDialog = () => {
        this.setState({dialogOpen: false});
    }

    render() {
        return (
            <Box className={"bodyBackground"}>
                <Box
                    sx={{display:"flex", width:"100%", flexDirection: "column", gap: "30px", paddingTop:"8px", height: "100%", margin:0, alignItems:"center"}}>
                        <Box sx={{display:"flex", width:"100%", flexDirection: "column", gap: "30px", padding: "8px", maxWidth:"none", marginTop:"20%", paddingLeft:"10%"}}>
                            <Box sx={{width:"30%"}}>
                                <Typography variant="h3" sx={{color:"#141414"}}>LOOK FOR RECIPES</Typography>
                            </Box>
                            <Box sx={{display:"flex", width:"100%", flexDirection: "row", gap:"20px"}}>
                                <Button className="mainButton" type="text" size="large" onClick={this.handleSearchByNameClick}>Search Recipe by Name</Button>
                                <Button className="mainButton" type="text" size="large" onClick={this.handleSearchByImageClick}>Search by Uploading Image</Button>
                            </Box>
                        </Box>
                </Box>
            </Box>
        );
    }
}

export default withRouter(HomeComponent);