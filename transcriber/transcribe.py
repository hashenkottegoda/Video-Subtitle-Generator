import whisper
import srt
import subprocess
from datetime import timedelta
import os

def extract_audio(video_path, audio_path):
    # Use ffmpeg directly
    command = [
        "ffmpeg",
        "-i", video_path,
        "-ar", "16000",
        "-ac", "1",
        "-f", "wav",
        audio_path
    ]
    subprocess.run(command, check=True)

def generate_srt(transcription, srt_path):
    subtitles = []
    for i, segment in enumerate(transcription['segments']):
        subtitles.append(srt.Subtitle(
            index=i + 1,
            start=timedelta(seconds=segment['start']),
            end=timedelta(seconds=segment['end']),
            content=segment['text'].strip()
        ))

    with open(srt_path, 'w') as f:
        f.write(srt.compose(subtitles))

def process_video(video_path):
    audio_path = video_path.replace(".mp4", ".wav")
    srt_path = video_path.replace(".mp4", ".srt")

    extract_audio(video_path, audio_path)

    model = whisper.load_model("base")
    result = model.transcribe(audio_path)
    generate_srt(result, srt_path)

    return srt_path
