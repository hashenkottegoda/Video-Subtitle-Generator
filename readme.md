# Video Subtitle Generator 🎬

This project is a full-stack, Dockerized application that automatically generates subtitle files (`.srt`) from video files using OpenAI's Whisper model. Users can upload `.mp4` video files through a REST API, and the system extracts audio, transcribes speech, and returns a downloadable subtitle file.

It's especially useful for:

- Content creators needing quick subtitles
- Lecture video archiving
- Accessibility automation
- Developers experimenting with Whisper-based transcription

---

### How it works

The application consists of two main services working together:

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

Both services share the same filesystem directory so they can read/write the same video and subtitle files without needing to exchange binary data over the network.

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

---

### How to Run It (Using Only Docker)

You do **not** need to install Maven or Java locally. Everything is handled inside Docker.

1. **Clone the project**

```bash
git clone https://github.com/hashenkottegoda/Video-Subtitle-Generator.git
cd video-subtitle-generator
```

2. **Start everything using Docker Compose**

```bash
docker-compose up --build
```

This will:

- Build the Java Spring Boot backend using Maven inside Docker
- Build the Python Whisper Flask container
- Start both services:
  - `http://localhost:8080` → Spring Boot API
  - `http://localhost:5001` → Python transcription service
- Mount the `./uploads/` folder to `/uploads` in both containers

---

### How to use it

You can interact with the app using `curl`, Postman, or your own frontend.

#### Option 1: Upload a new video file

```bash
curl -F "file=@/path/to/your/video.mp4" http://localhost:8080/api/upload
```

- The backend saves the file to `/uploads`
- It then calls the Python transcription service
- A `.srt` subtitle file is generated and saved
- You'll get a response like:

```
Subtitle ready: /api/download?file=your_video.srt
```

#### Option 2: Manually place a file in `/uploads` and trigger

1. Place a video in the `uploads/` folder:

```bash
cp somefile.mp4 ./uploads/
```

2. Trigger the transcription directly:

```bash
curl -X POST http://localhost:5001/transcribe \
  -H "Content-Type: application/json" \
  -d '{"video_path": "/uploads/somefile.mp4"}'
```

3. Download the subtitle file:

```bash
http://localhost:8080/api/download?file=somefile.srt
```

### File Structure Overview

```
video-subtitle-generator/
├── backend/              → Spring Boot API
├── transcriber/          → Python Flask + Whisper service
├── uploads/              → Shared volume for video/srt files
└── docker-compose.yml    → Runs both services together
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
