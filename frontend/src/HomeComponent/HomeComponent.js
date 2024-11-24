import React, {Component} from "react";
import "./HomeComponent.css";
import {withRouter} from "../withRouter/withRouter";

class HomeComponent extends Component {
    constructor(props) {
        super(props);
        this.state = {
            image: null,
            previewURL: null,
            name: null,
            loading: false
        };
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
                    return response.json();
                }).then(data => {
                    this.setState({loading: false});
                    this.props.navigate("/result", {state: {data: data}});
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

    inputText = async() => {
        const name = document.getElementById("nameUpload").value;
        if(name === "" || name === null) {
            alert("Please input a name");
            return
        }
        this.setState({
            name: name,
            loading: true
        });

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

    render() {
        return (
            <div>
                <h1>Home</h1>
                <div id={"content"}>
                    {this.state.loading === false && <div id={"image_section"}>
                        <h2>Upload an dish image</h2>
                        <label id="inputImageLabel" htmlFor="imageUpload" style={{border: "1px solid black"}}>Upload an Image</label>
                        <input id="imageUpload"
                               type="file"
                               accept="image/*"
                               onChange={this.uploadImage}/>
                    </div>}
                    {this.state.loading === false && <div id={"name_section"}>
                        <h2>Input a dish name</h2>
                        <input id="nameUpload" type="text"/>
                        <button onClick={this.inputText}>Upload</button>
                    </div>}
                    {this.state.loading && <div id={"loading_section"}>
                        <h2>Processing...</h2>
                        <div className="spinner"></div>
                    </div>}
                </div>
            </div>
        );
    }
}

export default withRouter(HomeComponent);