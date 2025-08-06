from flask import Flask, request, jsonify
from transcribe import process_video

app = Flask(__name__)

@app.route("/transcribe", methods=["POST"])
def transcribe():
    data = request.get_json()
    video_path = data.get("video_path")

    if not video_path:
        return jsonify({"error": "Missing video_path"}), 400

    try:
        srt_path = process_video(video_path)
        return jsonify({"srt_path": srt_path}), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5001)
