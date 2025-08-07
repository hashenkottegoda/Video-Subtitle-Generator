import React, { useState } from "react";
import axios from "axios";
import "./App.css";

function App() {
  const [file, setFile] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [downloadLink, setDownloadLink] = useState("");
  const [message, setMessage] = useState("");

  const handleFileChange = (e) => {
    setFile(e.target.files[0]);
    setDownloadLink("");
    setMessage("");
  };

  const handleUpload = async () => {
    if (!file) {
      setMessage("Please select a video file to upload.");
      return;
    }

    setIsLoading(true);
    setMessage("Uploading and generating subtitles...");

    const formData = new FormData();
    formData.append("file", file);

    try {
      const response = await axios.post(
        "http://localhost:8080/api/upload",
        formData,
        {
          headers: { "Content-Type": "multipart/form-data" },
        }
      );

      const { subtitle, download } = response.data;
      setDownloadLink("http://localhost:8080" + download);
      setMessage(`✅ Subtitles generated: ${subtitle}`);
    } catch (err) {
      console.error(err);
      setMessage("❌ Error generating subtitles.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="app-page">
      <div className="app-card">
        <h2 className="app-title">🎬 Video Subtitle Generator</h2>
        <input
          type="file"
          accept="video/mp4"
          onChange={handleFileChange}
          className="app-input"
        />
        <button
          onClick={handleUpload}
          disabled={isLoading}
          className="app-button"
        >
          {isLoading ? (
            <span>
              <span className="spinner" /> Processing...
            </span>
          ) : (
            "Generate Subtitles"
          )}
        </button>
        {message && <p className="app-message">{message}</p>}
        {downloadLink && (
          <a href={downloadLink} download className="app-link">
            ⬇️ Download Subtitle File
          </a>
        )}
      </div>
    </div>
  );
}

export default App;
