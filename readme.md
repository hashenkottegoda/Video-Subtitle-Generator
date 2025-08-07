# Video Subtitle Generator 🎬

A full-stack, Dockerized application that automatically generates subtitle files (`.srt`) from `.mp4` video files using OpenAI Whisper. Includes a simple web UI for uploading and downloading subtitles with one click.

---

## 💡 Features

- Upload video via React web app
- Generates subtitles using Whisper (via Python service)
- Download `.srt` file from browser
- Runs fully in Docker
- No manual steps or API calls needed

---

### How it works

The application consists of three main services working together:

1. **Spring Boot Backend (Java)**

   - Provides a REST API to upload video files
   - Saves videos into a shared volume (`/uploads`)
   - Triggers the transcription service (Python) via an internal HTTP call
   - Serves the generated `.srt` file for download

2. **Python Whisper Service (Flask)**

   - Listens for transcription requests
   - Uses `ffmpeg` to extract audio from `.mp4`
   - Uses OpenAI's `whisper` to transcribe audio
   - Formats the transcription as `.srt` using the `srt` Python library
   - Saves the subtitle file back to `/uploads`

3. **React Frontend**

   - Uploads `.mp4` files to the backend
   - Shows progress/loading state
   - Displays download link when subtitles are ready

All three containers share the same filesystem directory so they can read/write the same video and subtitle files without needing to exchange binary data over the network.

---

### Technologies Used

- Java 17 + Spring Boot 3.x
- Python 3.10
- Flask (microservice for transcription)
- OpenAI Whisper (speech-to-text)
- ffmpeg + ffmpeg-python (audio extraction)
- `srt` Python module (subtitle formatting)
- Video/Audio: `.mp4` input → `.wav` audio
- Docker + Docker Compose (multi-service orchestration)
- React 19 (frontend)

---

### How to Run It (Using Only Docker)

You do **not** need to install Maven or Java locally. Everything is handled inside Docker.

1. **Clone the project**

```bash
git clone https://github.com/hashenkottegoda/Video-Subtitle-Generator.git
```

2. **Start everything using Docker Compose**

```bash
docker-compose up --build
```

This will:

- Build the Java Spring Boot backend using Maven inside Docker
- Build the Python Whisper Flask container
- Build the React frontend
- Start all three services:

  - `http://localhost:8080` → Spring Boot API
  - `http://localhost:5001` → Python transcription
    service
  - `http://localhost:3000` → React frontend

- Mount the `./uploads/` folder to `/uploads` in all containers

---

### How to use it

1. **Visit http://localhost:3000**

2. **Upload a .mp4 file using the interface**

3. **Wait while the backend and transcriber process it**

4. **Download the .srt subtitle file once ready**

### File Structure Overview

```
video-subtitle-generator/
├── backend/              → Spring Boot API (Java)
├── transcriber/          → Python + Whisper + Flask
├── frontend/             → React frontend
├── uploads/              → Shared dir for videos + subtitles
└── docker-compose.yml    → Launches all 3 services
```

---

### License

This project is released under the MIT License. You're free to use, modify, and distribute it for personal or commercial purposes.

---

### Credits

- OpenAI Whisper — speech-to-text engine
- Spring Boot — reliable REST server
- Docker — for orchestration
- ffmpeg — for audio processing
- Python’s `srt` and Flask libraries
- React — for the web frontend
