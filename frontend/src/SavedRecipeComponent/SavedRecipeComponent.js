import Box from "@mui/material/Box";
import * as React from "react";
import {Button, Card, Space} from "antd";
import {DeleteOutlined, EditOutlined} from "@ant-design/icons";
import "./SavedRecipeComponent.css";

const truncateDescription = (description) => {
    const words = description.split(" ");
    return words.length > 10 ? words.slice(0, 10).join(" ") + "..." : description;
};

const data = Array.from({ length: 10 }, (_, i) => `Card ${i + 1}`);
const { Meta } = Card;
const handleImageClick = () => {
    alert("Image clicked!"); // Replace with your desired action
};
function SavedRecipeComponent () {
    return (
        <Box sx={{height: "100%", display:"flex", marginTop:"3%", flexDirection:"column", alignItems:"center", minHeight:"100vh", paddingBottom:"5%"}}>
            <Space wrap size="large" style={{ display: "flex", width:"80%", alignItems:"center"}}>
                {data.map((item, index) => (
                    <Card
                        hoverable
                        style={{
                            width: 240,
                        }}
                        cover={
                        <div className={"cardImageContainer"}>
                            <img className="cardImage" alt="example" src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR6eHuy9sEE411Snm88vZvNpDd_0Ja9tyXjS7yLdA-Im5_aCbIrVA" onClick={handleImageClick}/>
                        </div>
                        }
                        actions={[
                            <EditOutlined></EditOutlined>,
                            <DeleteOutlined></DeleteOutlined>
                        ]}
                    >
                        <Meta
                            title="Card title"
                            description={
                                <div className="cardDescription">
                                    {truncateDescription(
                                        "This is a long description that will wrap and break ."
                                    )}
                                </div>
                            }
                            onClick={handleImageClick}
                        />
                    </Card>
                ))}
            </Space>
        </Box>
    );
}

export default SavedRecipeComponent;