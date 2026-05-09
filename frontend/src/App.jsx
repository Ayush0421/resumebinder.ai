import { BrowserRouter, Routes, Route, useNavigate } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import { useState, useRef } from 'react';
import { Upload, FileText, Wand2, Download, CheckCircle2 } from 'lucide-react';
import './index.css';

// ===================
// LANDING PAGE
// ===================
function LandingPage() {
  const navigate = useNavigate();

  return (
    <div className="landing-container">
      <motion.div 
        initial={{ opacity: 0, y: 30 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.8, ease: "easeOut" }}
        className="landing-content"
      >
        <div className="logo-badge">Resumebinder.ai</div>
        <h1 className="hero-title">Hack the ATS.<br/>Land the Interview.</h1>
        <p className="hero-subtitle">
          Upload your standard Overleaf LaTeX resume. We extract the data, cross-reference the Job Description, and reorganize your impact automatically.
        </p>
        <motion.button 
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
          className="get-started-btn"
          onClick={() => navigate('/builder')}
        >
          <Wand2 size={20} />
          Get Started
        </motion.button>
      </motion.div>
    </div>
  );
}

// ===================
// JOURNEY PAGE
// ===================
function BuilderJourney() {
  const [step, setStep] = useState(1);
  const [file, setFile] = useState(null);
  const [rawResume, setRawResume] = useState("");
  const [jd, setJd] = useState("");
  const [loadingHook, setLoadingHook] = useState("");
  const [result, setResult] = useState(null);
  
  const printRef = useRef();

  const handleFileUpload = async (e) => {
    const uploadedFile = e.target.files[0];
    if (!uploadedFile) return;
    setFile(uploadedFile);

    const formData = new FormData();
    formData.append('file', uploadedFile);

    const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

    try {
      const res = await fetch(`${API_BASE_URL}/api/resume/extract-pdf`, {
        method: 'POST',
        body: formData
      });
      const data = await res.json();
      if (data.error) throw new Error(data.error);
      setRawResume(data.rawText);
      setStep(2); // Move to JD step
    } catch (err) {
      alert("Error reading PDF: " + err.message);
      setFile(null);
    }
  };

  const handleGenerate = async () => {
    setStep(3); // Loading step
    const hooks = [
      "Injecting premium keywords...",
      "Re-aligning your achievements...",
      "Hacking the ATS algorithms...",
      "Formatting LaTeX structure..."
    ];
    let i = 0;
    const interval = setInterval(() => {
      setLoadingHook(hooks[i % hooks.length]);
      i++;
    }, 1500);

    try {
      const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';
      const res = await fetch(`${API_BASE_URL}/api/resume/generate`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ jd, rawResume })
      });
      const data = await res.json();
      if (data.error) throw new Error(data.error);
      setResult(data);
      clearInterval(interval);
      setStep(4); // Result step
    } catch (err) {
      clearInterval(interval);
      alert("Error generating resume: " + err.message);
      setStep(2);
    }
  };

  const handlePrint = () => {
    if (!result || !result.html) return;
    const printWindow = window.open('', '_blank');
    printWindow.document.write(result.html);
    printWindow.document.close();
    printWindow.focus();
    setTimeout(() => {
        printWindow.print();
        printWindow.close();
    }, 250);
  };

  return (
    <div className="builder-container">
      <div className="stepper">
        <div className={`step-dot ${step >= 1 ? 'active' : ''}`} />
        <div className={`step-line ${step >= 2 ? 'active' : ''}`} />
        <div className={`step-dot ${step >= 2 ? 'active' : ''}`} />
        <div className={`step-line ${step >= 4 ? 'active' : ''}`} />
        <div className={`step-dot ${step >= 4 ? 'active' : ''}`} />
      </div>

      <AnimatePresence mode="wait">
        
        {/* STEP 1: UPLOAD */}
        {step === 1 && (
          <motion.div 
            key="step1"
            initial={{ opacity: 0, x: 50 }} animate={{ opacity: 1, x: 0 }} exit={{ opacity: 0, x: -50 }}
            className="step-card"
          >
            <h2>Upload Existing Resume</h2>
            <p>We accept your existing Overleaf/LaTeX PDF.</p>
            <label className="upload-zone">
              <Upload size={40} className="upload-icon" />
              <span>Click to browse or drag PDF here</span>
              <input type="file" accept="application/pdf" onChange={handleFileUpload} hidden />
            </label>
          </motion.div>
        )}

        {/* STEP 2: JOB DESCRIPTION */}
        {step === 2 && (
          <motion.div 
            key="step2"
            initial={{ opacity: 0, x: 50 }} animate={{ opacity: 1, x: 0 }} exit={{ opacity: 0, x: -50 }}
            className="step-card"
          >
            <div className="success-badge"><CheckCircle2 size={16} /> PDF Extracted</div>
            <h2>Target Job Description</h2>
            <p>Paste the JD so our AI can align your experiences perfectly.</p>
            <textarea 
              className="jd-box"
              placeholder="Requirements: 3+ years Java, React..."
              value={jd}
              onChange={e => setJd(e.target.value)}
            />
            <button className="primary-btn" onClick={handleGenerate} disabled={!jd}>
              Analyze & Rebuild
            </button>
          </motion.div>
        )}

        {/* STEP 3: LOADING HOOK */}
        {step === 3 && (
          <motion.div 
             key="step3"
             initial={{ opacity: 0, scale: 0.9 }} animate={{ opacity: 1, scale: 1 }} exit={{ opacity: 0, scale: 1.1 }}
             className="loading-overlay"
          >
            <div className="spinner-large" />
            <motion.h2
              key={loadingHook}
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -10 }}
            >
              {loadingHook || "Processing..."}
            </motion.h2>
          </motion.div>
        )}

        {/* STEP 4: PREVIEW & DOWNLOAD */}
        {step === 4 && result && (
          <motion.div 
            key="step4"
            initial={{ opacity: 0, y: 50 }} animate={{ opacity: 1, y: 0 }}
            className="result-container"
          >
            <div className="result-header">
              <h2>Optimization Complete!</h2>
              <div className="score-badge">ATS Score: {result.atsScore}/100</div>
              <button className="download-btn" onClick={handlePrint}>
                <Download size={18} /> Download PDF
              </button>
            </div>
            
            <div className="preview-layout">
              <div className="tips-panel">
                <h3>Keywords Unmatched</h3>
                <div className="tags">
                  {result.keywordMatchAnalysis?.missingKeywords?.map(kw => (
                    <span key={kw} className="tag missing">{kw}</span>
                  ))}
                </div>
                <h3 style={{marginTop: '20px'}}>Actionable Advice</h3>
                <ul className="advice-list">
                  {result.improvementSuggestions?.map((s, i) => <li key={i}>{s}</li>)}
                </ul>
              </div>

              <div className="iframe-wrapper">
                <iframe srcDoc={result.html} title="Resume" />
              </div>
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}

// ===================
// APP ROOT
// ===================
export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<LandingPage />} />
        <Route path="/builder" element={<BuilderJourney />} />
      </Routes>
    </BrowserRouter>
  );
}
