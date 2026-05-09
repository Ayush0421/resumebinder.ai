# ResumeBinder.ai

ResumeBinder.ai is a full-stack, AI-driven resume generation platform designed to help job seekers bypass Applicant Tracking Systems (ATS) and land interviews at top tech companies. Utilizing a Java Spring Boot backend integrated with Google's Gemini API, it intelligently restructures and optimizes raw resume data using a specialized "Product-Based Company HR" persona. 

The application features a sleek React frontend for seamless data entry and utilizes server-side rendering to produce pixel-perfect, LaTeX-inspired, single-page PDF documents.

## 🚀 Key Features

*   🧠 **AI-Powered Tailoring:** Uses Gemini AI (2.5 Flash) to rewrite and reorder bullet points to match specific Job Descriptions.
*   📄 **Pixel-Perfect Formatting:** Leverages Thymeleaf for server-side HTML-to-PDF rendering, ensuring a strict, professional one-page layout without overflow.
*   🛡️ **Robust & Reliable:** Features an automatic exponential backoff retry mechanism to handle API rate limits and ensure successful generation during peak demand.
*   ✨ **Modern UI:** Responsive React frontend with `framer-motion` animations, Markdown rendering, and a sleek design.

## 🛠️ Tech Stack

**Frontend:**
* React 19 (Vite)
* Framer Motion (Animations)
* Lucide React (Icons)
* React Markdown

**Backend:**
* Java 17+
* Spring Boot 3+
* Thymeleaf (Server-side rendering templates)
* Google Gemini API

## ⚙️ Local Development Setup

### 1. Clone the repository
```bash
git clone https://github.com/Ayush0421/resumebinder.ai.git
cd resumebinder.ai
```

### 2. Backend Setup
1. Navigate to the backend directory:
   ```bash
   cd backend
   ```
2. Open `src/main/resources/application.properties` and replace the placeholder with your actual Gemini API Key:
   ```properties
   gemini.api.key=YOUR_GEMINI_API_KEY
   ```
3. Run the Spring Boot application (using Maven):
   ```bash
   ./mvnw spring-boot:run
   ```
   *The backend will start on `http://localhost:8080`.*

### 3. Frontend Setup
1. Open a new terminal and navigate to the frontend directory:
   ```bash
   cd frontend
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Start the development server:
   ```bash
   npm run dev
   ```
   *The frontend will be available at `http://localhost:5173` (or the port Vite provides).*

## 📝 Usage
1. Open the frontend application in your browser.
2. Paste your raw resume text or details into the provided input area.
3. Paste the target Job Description (JD) you want to tailor your resume for.
4. Click **Generate**. The AI will restructure and optimize your resume using an HR-focused persona.
5. Preview the result and download your fully-formatted, single-page PDF resume.
