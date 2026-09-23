package com.lab;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.lab.bean.StudentBean;
import com.lab.dao.StudentDAO;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class WebServerApp {
    private static final int PORT = System.getenv("PORT") != null 
        ? Integer.parseInt(System.getenv("PORT")) 
        : 8080;
    private static final StudentDAO dao = new StudentDAO();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/", new StaticFileHandler());
        server.createContext("/api/students", new ApiHandler());

        server.setExecutor(null);
        System.out.println("=================================================");
        System.out.println(" Server running at: http://localhost:" + PORT);
        System.out.println("=================================================");
        server.start();
    }

    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            
            if ("/favicon.ico".equals(path)) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            String html = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Student Portal | Cyber Terminal Edition</title>
                    <link rel="preconnect" href="https://fonts.googleapis.com">
                    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
                    <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;500;700&family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap" rel="stylesheet">
                    <style>
                        :root {
                            --bg: #040711;
                            --glass-bg: rgba(11, 17, 33, 0.6);
                            --glass-border: rgba(255, 255, 255, 0.1);
                            --glass-border-light: rgba(255, 255, 255, 0.22);
                            --glass-highlight: inset 0 1px 1px 0 rgba(255, 255, 255, 0.2);
                            --glass-shadow: 0 25px 60px -15px rgba(0, 0, 0, 0.8);
                            
                            --cyan: #38bdf8;
                            --green: #22c55e;
                            --indigo: #6366f1;
                            --accent-grad: linear-gradient(135deg, #38bdf8 0%, #6366f1 50%, #a855f7 100%);
                            
                            --text-main: #f8fafc;
                            --text-muted: #94a3b8;
                            --danger: #f43f5e;
                            --warning: #fbbf24;
                        }

                        * { box-sizing: border-box; margin: 0; padding: 0; font-family: 'Plus Jakarta Sans', sans-serif; }
                        
                        body {
                            background-color: var(--bg);
                            background-image: 
                                radial-gradient(circle at 50% -20%, rgba(56, 189, 248, 0.18) 0%, transparent 60%),
                                linear-gradient(rgba(255, 255, 255, 0.035) 1px, transparent 1px),
                                linear-gradient(90deg, rgba(255, 255, 255, 0.035) 1px, transparent 1px);
                            background-size: 100% 100%, 32px 32px, 32px 32px;
                            background-position: 0 0, -1px -1px, -1px -1px;
                            color: var(--text-main);
                            min-height: 100vh;
                            display: flex;
                            flex-direction: column;
                            position: relative;
                            overflow-x: hidden;
                        }

                        /* Geometric Background Animations */
                        .gshape-canvas {
                            position: fixed;
                            inset: 0;
                            pointer-events: none;
                            z-index: 0;
                            overflow: hidden;
                        }
                        .g-shape {
                            position: absolute;
                            border: 1px solid rgba(56, 189, 248, 0.2);
                            background: linear-gradient(135deg, rgba(56, 189, 248, 0.04), rgba(99, 102, 241, 0.06));
                            backdrop-filter: blur(2px);
                            box-shadow: 0 0 40px rgba(56, 189, 248, 0.12);
                        }
                        .shape-diamond {
                            width: 170px; height: 170px;
                            top: 10%; left: 6%;
                            border-radius: 28px;
                            animation: floatRotate 24s infinite linear;
                        }
                        .shape-ring {
                            width: 320px; height: 320px;
                            bottom: 5%; right: 4%;
                            border-radius: 45%;
                            border-width: 2px;
                            border-color: rgba(99, 102, 241, 0.25);
                            animation: floatSpin 32s infinite reverse linear;
                        }
                        .shape-box {
                            width: 120px; height: 120px;
                            top: 40%; right: 12%;
                            border-radius: 20px;
                            border-color: rgba(168, 85, 247, 0.25);
                            animation: floatBob 18s infinite ease-in-out alternate;
                        }

                        @keyframes floatRotate {
                            0% { transform: rotate(0deg) translate(0, 0); }
                            50% { transform: rotate(180deg) translate(30px, -30px); }
                            100% { transform: rotate(360deg) translate(0, 0); }
                        }
                        @keyframes floatSpin {
                            0% { transform: rotate(0deg) scale(1); }
                            50% { transform: rotate(180deg) scale(1.08); }
                            100% { transform: rotate(360deg) scale(1); }
                        }
                        @keyframes floatBob {
                            0% { transform: translateY(0px) rotate(15deg); }
                            100% { transform: translateY(-50px) rotate(45deg); }
                        }

                        /* Universal Glass Frame */
                        .glass-card {
                            background: var(--glass-bg);
                            backdrop-filter: blur(28px) saturate(190%);
                            -webkit-backdrop-filter: blur(28px) saturate(190%);
                            border: 1px solid var(--glass-border);
                            border-top: 1px solid var(--glass-border-light);
                            border-left: 1px solid var(--glass-border-light);
                            border-radius: 20px;
                            box-shadow: var(--glass-shadow), var(--glass-highlight);
                            position: relative;
                            z-index: 2;
                        }

                        /* Header */
                        header {
                            position: sticky;
                            top: 0;
                            z-index: 100;
                            padding: 1.25rem 1.5rem;
                        }
                        .header-inner {
                            max-width: 1140px;
                            margin: 0 auto;
                            padding: 0.85rem 1.5rem;
                            display: flex;
                            align-items: center;
                            justify-content: space-between;
                        }
                        .brand { display: flex; align-items: center; gap: 0.85rem; }
                        .brand-icon {
                            width: 42px; height: 42px;
                            background: var(--accent-grad);
                            border-radius: 12px;
                            display: grid; place-items: center;
                            font-weight: 800; font-size: 1.2rem;
                            color: #040711;
                            box-shadow: 0 4px 18px rgba(56, 189, 248, 0.45);
                            border: 1px solid rgba(255, 255, 255, 0.4);
                        }
                        .brand-text h1 {
                            font-size: 1.15rem; font-weight: 800; letter-spacing: -0.02em;
                            background: linear-gradient(135deg, #fff 30%, #bae6fd 100%);
                            -webkit-background-clip: text;
                            -webkit-text-fill-color: transparent;
                        }
                        .brand-text span {
                            font-size: 0.72rem; color: var(--text-muted); font-weight: 600; text-transform: uppercase; letter-spacing: 0.05em;
                        }

                        .nav-tabs {
                            display: flex;
                            background: rgba(11, 17, 32, 0.65);
                            padding: 4px;
                            border-radius: 12px;
                            border: 1px solid var(--glass-border);
                            gap: 4px;
                        }
                        .nav-btn {
                            background: transparent;
                            border: none;
                            color: var(--text-muted);
                            padding: 0.6rem 1.25rem;
                            font-size: 0.85rem;
                            font-weight: 600;
                            border-radius: 8px;
                            cursor: pointer;
                            transition: all 0.2s ease;
                            display: flex;
                            align-items: center;
                            gap: 0.5rem;
                        }
                        .nav-btn:hover { color: #fff; }
                        .nav-btn.active {
                            background: var(--accent-grad);
                            color: #040711;
                            font-weight: 800;
                            box-shadow: 0 4px 14px rgba(56, 189, 248, 0.45);
                        }
                        .badge-count {
                            background: rgba(4, 7, 17, 0.4);
                            padding: 2px 7px;
                            border-radius: 999px;
                            font-size: 0.72rem;
                            font-weight: 800;
                        }

                        /* Main Content Area */
                        main {
                            flex: 1;
                            max-width: 1140px;
                            width: 100%;
                            margin: 1.5rem auto 3rem auto;
                            padding: 0 1.5rem;
                            position: relative;
                            z-index: 10;
                        }
                        .view-section { display: none; }
                        .view-section.active { display: block; animation: fadeIn 0.25s ease-out; }

                        @keyframes fadeIn {
                            from { opacity: 0; transform: translateY(8px); }
                            to { opacity: 1; transform: translateY(0); }
                        }

                        /* =========================================================
                           TWO-COLUMN HOME: REGISTRATION + TERMINAL CONSOLE
                           ========================================================= */
                        .register-layout {
                            display: grid;
                            grid-template-columns: 1.15fr 1fr;
                            gap: 2rem;
                            max-width: 1050px;
                            margin: 1rem auto;
                            align-items: stretch;
                        }
                        @media (max-width: 900px) {
                            .register-layout { grid-template-columns: 1fr; max-width: 540px; }
                        }

                        /* Beam Animated Card */
                        .beam-card-container {
                            position: relative;
                            border-radius: 22px;
                            padding: 2px;
                            overflow: hidden;
                            background: rgba(255, 255, 255, 0.05);
                            box-shadow: 0 25px 60px -15px rgba(0, 0, 0, 0.85);
                            height: 100%;
                        }
                        .beam-card-container::before {
                            content: '';
                            position: absolute;
                            top: -50%; left: -50%;
                            width: 200%; height: 200%;
                            background: conic-gradient(transparent, rgba(56, 189, 248, 0.6), rgba(168, 85, 247, 0.6), transparent 45%);
                            animation: rotateBeam 6s linear infinite;
                            z-index: 0;
                        }
                        @keyframes rotateBeam {
                            0% { transform: rotate(0deg); }
                            100% { transform: rotate(360deg); }
                        }

                        .form-inner {
                            position: relative;
                            z-index: 1;
                            background: rgba(11, 16, 31, 0.85);
                            backdrop-filter: blur(32px);
                            -webkit-backdrop-filter: blur(32px);
                            border-radius: 20px;
                            padding: 2.5rem 2.25rem;
                            height: 100%;
                            display: flex;
                            flex-direction: column;
                            justify-content: space-between;
                        }

                        .pill-chip {
                            display: inline-flex;
                            align-items: center;
                            gap: 0.4rem;
                            background: rgba(56, 189, 248, 0.12);
                            border: 1px solid rgba(56, 189, 248, 0.3);
                            color: #7dd3fc;
                            font-size: 0.72rem;
                            font-weight: 700;
                            text-transform: uppercase;
                            letter-spacing: 0.06em;
                            padding: 0.3rem 0.75rem;
                            border-radius: 999px;
                            margin-bottom: 0.75rem;
                        }
                        .pill-dot { width: 6px; height: 6px; border-radius: 50%; background: #38bdf8; box-shadow: 0 0 8px #38bdf8; animation: pulseDot 2s infinite; }
                        @keyframes pulseDot { 0%, 100% { opacity: 1; } 50% { opacity: 0.4; } }

                        .form-title {
                            font-size: 1.6rem;
                            font-weight: 800;
                            letter-spacing: -0.02em;
                            background: linear-gradient(135deg, #ffffff 40%, #93c5fd 100%);
                            -webkit-background-clip: text;
                            -webkit-text-fill-color: transparent;
                        }
                        .form-sub { font-size: 0.85rem; color: var(--text-muted); margin-top: 0.3rem; margin-bottom: 1.5rem; }

                        .interactive-field {
                            position: relative;
                            margin-bottom: 1.25rem;
                        }
                        .input-icon {
                            position: absolute;
                            left: 1rem;
                            top: 50%;
                            transform: translateY(-50%);
                            color: var(--text-muted);
                            transition: all 0.25s ease;
                            pointer-events: none;
                        }
                        .modern-input {
                            width: 100%;
                            padding: 0.9rem 1rem 0.9rem 2.75rem;
                            background: rgba(8, 12, 22, 0.55);
                            border: 1px solid var(--glass-border);
                            border-radius: 12px;
                            color: #fff;
                            font-size: 0.92rem;
                            transition: all 0.25s cubic-bezier(0.2, 0.8, 0.2, 1);
                        }
                        .modern-input::placeholder { color: rgba(148, 163, 184, 0.4); }
                        .modern-input:focus {
                            outline: none;
                            background: rgba(15, 23, 42, 0.85);
                            border-color: var(--cyan);
                            box-shadow: 0 0 0 3px rgba(56, 189, 248, 0.2), 0 0 20px rgba(56, 189, 248, 0.2);
                            transform: translateY(-1px);
                        }
                        .modern-input:focus + .input-icon {
                            color: var(--cyan);
                            transform: translateY(-50%) scale(1.1);
                        }

                        .btn-neon-action {
                            width: 100%;
                            position: relative;
                            background: var(--accent-grad);
                            color: #040711;
                            border: none;
                            padding: 1rem 1.5rem;
                            font-size: 0.95rem;
                            font-weight: 800;
                            border-radius: 12px;
                            cursor: pointer;
                            transition: all 0.25s ease;
                            display: flex;
                            align-items: center;
                            justify-content: center;
                            gap: 0.6rem;
                            box-shadow: 0 10px 25px rgba(56, 189, 248, 0.4);
                            overflow: hidden;
                        }
                        .btn-neon-action:hover {
                            box-shadow: 0 14px 35px rgba(56, 189, 248, 0.6);
                            transform: translateY(-2px);
                            filter: brightness(1.08);
                        }

                        /* =========================================================
                           HACKER / LINUX STYLE TERMINAL (RECENT RECORDS FEED)
                           ========================================================= */
                        .terminal-window {
                            background: #060a14;
                            border: 1px solid rgba(56, 189, 248, 0.25);
                            border-radius: 16px;
                            overflow: hidden;
                            box-shadow: 0 20px 50px rgba(0, 0, 0, 0.85), inset 0 1px 1px rgba(255, 255, 255, 0.1);
                            display: flex;
                            flex-direction: column;
                            height: 100%;
                        }

                        /* Terminal Titlebar */
                        .terminal-header {
                            background: #0b1120;
                            padding: 0.75rem 1rem;
                            border-bottom: 1px solid rgba(255, 255, 255, 0.08);
                            display: flex;
                            align-items: center;
                            justify-content: space-between;
                        }
                        .terminal-dots {
                            display: flex;
                            gap: 6px;
                        }
                        .dot {
                            width: 11px;
                            height: 11px;
                            border-radius: 50%;
                        }
                        .dot-red { background: #ef4444; }
                        .dot-yellow { background: #eab308; }
                        .dot-green { background: #22c55e; }

                        .terminal-title {
                            font-family: 'JetBrains Mono', monospace;
                            font-size: 0.75rem;
                            color: #94a3b8;
                            letter-spacing: 0.03em;
                        }
                        .terminal-status {
                            display: flex;
                            align-items: center;
                            gap: 6px;
                            font-family: 'JetBrains Mono', monospace;
                            font-size: 0.7rem;
                            color: var(--green);
                            font-weight: 700;
                        }
                        .blink-circle {
                            width: 7px;
                            height: 7px;
                            border-radius: 50%;
                            background: var(--green);
                            box-shadow: 0 0 8px var(--green);
                            animation: pulseDot 1.5s infinite;
                        }

                        /* Terminal Body & Stream */
                        .terminal-body {
                            flex: 1;
                            padding: 1.25rem;
                            font-family: 'JetBrains Mono', monospace;
                            font-size: 0.82rem;
                            line-height: 1.6;
                            color: #cbd5e1;
                            overflow-y: auto;
                            max-height: 480px;
                            display: flex;
                            flex-direction: column;
                            gap: 0.75rem;
                        }
                        .terminal-body::-webkit-scrollbar { width: 5px; }
                        .terminal-body::-webkit-scrollbar-thumb { background: rgba(56, 189, 248, 0.2); border-radius: 4px; }

                        .prompt-line {
                            color: #38bdf8;
                            font-weight: 600;
                            display: flex;
                            align-items: center;
                            gap: 0.5rem;
                        }
                        .prompt-line span.path { color: #a855f7; }
                        .prompt-line span.cmd { color: #f8fafc; font-weight: 400; }

                        .record-entry {
                            background: rgba(15, 23, 42, 0.7);
                            border: 1px solid rgba(255, 255, 255, 0.06);
                            border-left: 3px solid var(--cyan);
                            border-radius: 8px;
                            padding: 0.65rem 0.85rem;
                            animation: termFade 0.3s ease-out;
                        }
                        .record-entry:first-child {
                            border-left-color: var(--green);
                            background: rgba(34, 197, 94, 0.05);
                        }
                        @keyframes termFade {
                            from { opacity: 0; transform: translateX(-6px); }
                            to { opacity: 1; transform: translateX(0); }
                        }

                        .entry-top {
                            display: flex;
                            justify-content: space-between;
                            align-items: center;
                            margin-bottom: 0.35rem;
                        }
                        .entry-id { color: var(--cyan); font-weight: 700; }
                        .entry-time { font-size: 0.72rem; color: #64748b; }
                        .entry-name { color: #fff; font-weight: 700; font-size: 0.9rem; }
                        .entry-details {
                            display: flex;
                            gap: 0.85rem;
                            font-size: 0.75rem;
                            color: #94a3b8;
                            margin-top: 0.25rem;
                        }
                        .badge-term-gpa { color: #34d399; font-weight: 700; }
                        .badge-term-course { color: #818cf8; }

                        .terminal-cursor {
                            display: inline-block;
                            width: 8px;
                            height: 15px;
                            background: var(--cyan);
                            vertical-align: middle;
                            animation: blinkCursor 1s infinite;
                            margin-left: 4px;
                        }
                        @keyframes blinkCursor {
                            0%, 49% { opacity: 1; }
                            50%, 100% { opacity: 0; }
                        }

                        /* =========================================================
                           DIRECTORY SCREEN
                           ========================================================= */
                        .stats-row {
                            display: grid;
                            grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
                            gap: 1.25rem;
                            margin-bottom: 2rem;
                        }
                        .stat-card {
                            padding: 1.35rem 1.6rem;
                            display: flex;
                            align-items: center;
                            gap: 1.25rem;
                        }
                        .stat-icon {
                            width: 48px; height: 48px;
                            border-radius: 12px;
                            display: grid; place-items: center;
                            font-size: 1.3rem;
                            border: 1px solid var(--glass-border-light);
                        }
                        .stat-icon-cyan { background: rgba(56, 189, 248, 0.15); color: var(--cyan); }
                        .stat-icon-purple { background: rgba(99, 102, 241, 0.15); color: #a5b4fc; }
                        .stat-info .stat-lbl {
                            font-size: 0.75rem; font-weight: 700; text-transform: uppercase; letter-spacing: 0.05em; color: var(--text-muted);
                        }
                        .stat-info .stat-num {
                            font-size: 1.65rem; font-weight: 800; color: #fff; margin-top: 0.15rem;
                        }

                        .dir-toolbar {
                            display: flex;
                            align-items: center;
                            justify-content: space-between;
                            margin-bottom: 1.25rem;
                            gap: 1rem;
                            flex-wrap: wrap;
                        }
                        .search-box { position: relative; max-width: 320px; width: 100%; }
                        .search-box input { padding-left: 2.4rem; }
                        .search-icon {
                            position: absolute; left: 0.85rem; top: 50%;
                            transform: translateY(-50%); color: var(--text-muted);
                        }

                        /* Glass Table */
                        .table-card { overflow: hidden; }
                        table { width: 100%; border-collapse: collapse; text-align: left; }
                        thead th {
                            background: rgba(8, 12, 24, 0.7);
                            padding: 0.95rem 1.2rem;
                            font-size: 0.75rem; font-weight: 700; text-transform: uppercase; letter-spacing: 0.06em;
                            color: var(--text-muted); border-bottom: 1px solid var(--glass-border);
                        }
                        tbody td {
                            padding: 1.05rem 1.2rem;
                            border-bottom: 1px solid rgba(255, 255, 255, 0.06);
                            font-size: 0.9rem; vertical-align: middle;
                        }
                        tbody tr:last-child td { border-bottom: none; }
                        tbody tr:hover td { background: rgba(255, 255, 255, 0.025); }

                        .id-tag { font-family: monospace; color: var(--text-muted); font-size: 0.82rem; }
                        .student-cell { display: flex; flex-direction: column; gap: 0.15rem; }
                        .student-name { font-weight: 700; color: #fff; }
                        .student-email { font-size: 0.8rem; color: var(--text-muted); }

                        .course-badge {
                            background: rgba(99, 102, 241, 0.16);
                            color: #c7d2fe;
                            border: 1px solid rgba(99, 102, 241, 0.3);
                            padding: 0.25rem 0.65rem;
                            border-radius: 999px;
                            font-size: 0.78rem; font-weight: 700;
                            display: inline-block;
                        }
                        .gpa-tag {
                            font-weight: 800; color: #38bdf8;
                            background: rgba(56, 189, 248, 0.12);
                            padding: 0.25rem 0.6rem; border-radius: 6px; font-size: 0.85rem;
                        }

                        .actions-cell { display: flex; gap: 0.45rem; justify-content: flex-end; }
                        .action-btn {
                            border: none; padding: 0.4rem 0.75rem; border-radius: 6px;
                            font-size: 0.8rem; font-weight: 700; cursor: pointer; transition: all 0.15s ease;
                        }
                        .btn-edit { background: rgba(251, 191, 36, 0.15); color: #fde047; border: 1px solid rgba(251, 191, 36, 0.3); }
                        .btn-edit:hover { background: #fbbf24; color: #040711; }
                        .btn-del { background: rgba(244, 63, 94, 0.15); color: #fda4af; border: 1px solid rgba(244, 63, 94, 0.3); }
                        .btn-del:hover { background: var(--danger); color: #fff; }

                        /* Edit Modal */
                        .modal-overlay {
                            position: fixed; inset: 0;
                            background: rgba(3, 7, 18, 0.75);
                            backdrop-filter: blur(16px);
                            -webkit-backdrop-filter: blur(16px);
                            display: none; align-items: center; justify-content: center;
                            z-index: 200; padding: 1.5rem;
                        }
                        .modal-overlay.open { display: flex; animation: fadeIn 0.2s ease-out; }
                        .modal-card { width: 100%; max-width: 480px; padding: 2.25rem; }
                        .modal-footer { display: flex; justify-content: flex-end; gap: 0.75rem; margin-top: 1.5rem; }
                        .btn-secondary {
                            background: rgba(255, 255, 255, 0.06);
                            border: 1px solid var(--glass-border);
                            color: var(--text-muted); padding: 0.7rem 1.2rem;
                            border-radius: 8px; font-size: 0.85rem; font-weight: 700; cursor: pointer;
                        }
                        .btn-secondary:hover { color: #fff; background: rgba(255, 255, 255, 0.12); }

                        /* Toast Alerts */
                        #toastStack {
                            position: fixed; bottom: 2rem; right: 2rem; z-index: 300;
                            display: flex; flex-direction: column; gap: 0.5rem;
                        }
                        .glass-toast {
                            background: rgba(13, 19, 36, 0.85);
                            backdrop-filter: blur(16px);
                            border: 1px solid var(--glass-border);
                            border-top: 1px solid var(--glass-border-light);
                            padding: 0.85rem 1.25rem; border-radius: 10px; color: #fff;
                            font-size: 0.875rem; font-weight: 600; box-shadow: 0 8px 24px rgba(0, 0, 0, 0.5);
                        }
                    </style>
                </head>
                <body>

                <!-- Animated Background Geometry -->
                <div class="gshape-canvas">
                    <div class="g-shape shape-diamond"></div>
                    <div class="g-shape shape-ring"></div>
                    <div class="g-shape shape-box"></div>
                </div>

                <header>
                    <div class="header-inner glass-card">
                        <div class="brand">
                            <div class="brand-icon">S</div>
                            <div class="brand-text">
                                <h1>StudentPortal</h1>
                                <span>JavaBean Architecture &bull; MySQL JDBC</span>
                            </div>
                        </div>
                        <nav class="nav-tabs">
                            <button id="btnTabRegister" class="nav-btn active" onclick="switchSection('register')">
                                <svg width="15" height="15" fill="none" stroke="currentColor" stroke-width="2.5" viewBox="0 0 24 24"><path d="M12 4v16m8-8H4"/></svg>
                                New Enrollment
                            </button>
                            <button id="btnTabDirectory" class="nav-btn" onclick="switchSection('directory')">
                                <svg width="15" height="15" fill="none" stroke="currentColor" stroke-width="2.5" viewBox="0 0 24 24"><path d="M4 6h16M4 12h16M4 18h7"/></svg>
                                Directory
                                <span id="lblNavCount" class="badge-count">0</span>
                            </button>
                        </nav>
                    </div>
                </header>

                <main>
                    <!-- SECTION 1: HOME (FORM + RECENT RECORDS TERMINAL) -->
                    <section id="secRegister" class="view-section active">
                        <div class="register-layout">
                            
                            <!-- Left: Form -->
                            <div class="beam-card-container">
                                <div class="form-inner">
                                    <div>
                                        <div class="pill-chip">
                                            <span class="pill-dot"></span>
                                            JavaBean &bull; Serializable
                                        </div>
                                        <h2 class="form-title">Student Registration</h2>
                                        <p class="form-sub">Insert into `students` table via StudentDAO</p>

                                        <form id="frmRegister">
                                            <div class="interactive-field">
                                                <input type="text" id="regName" class="modern-input" required placeholder="Full Name">
                                                <span class="input-icon">
                                                    <svg width="17" height="17" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                                                </span>
                                            </div>

                                            <div class="interactive-field">
                                                <input type="email" id="regEmail" class="modern-input" required placeholder="Email Address">
                                                <span class="input-icon">
                                                    <svg width="17" height="17" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/><polyline points="22,6 12,13 2,6"/></svg>
                                                </span>
                                            </div>

                                            <div class="interactive-field">
                                                <input type="text" id="regCourse" class="modern-input" required placeholder="Course / Department">
                                                <span class="input-icon">
                                                    <svg width="17" height="17" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path d="M12 14l9-5-9-5-9 5 9 5z"/><path d="M12 14l6.16-3.422a12.083 12.083 0 01.665 6.479A11.952 11.952 0 0012 20.055a11.952 11.952 0 00-6.824-2.998 12.078 12.078 0 01.665-6.479L12 14z"/></svg>
                                                </span>
                                            </div>

                                            <div class="interactive-field">
                                                <input type="number" step="0.01" min="0" max="10" id="regGpa" class="modern-input" required placeholder="GPA (e.g. 8.95)">
                                                <span class="input-icon">
                                                    <svg width="17" height="17" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>
                                                </span>
                                            </div>

                                            <button type="submit" class="btn-neon-action">
                                                <span>Commit to Database</span>
                                                <svg width="18" height="18" fill="none" stroke="currentColor" stroke-width="2.5" viewBox="0 0 24 24"><path d="M14 5l7 7m0 0l-7 7m7-7H3"/></svg>
                                            </button>
                                        </form>
                                    </div>
                                </div>
                            </div>

                            <!-- Right: Linux Hacker Style Live Terminal (Recent Records) -->
                            <div class="terminal-window">
                                <div class="terminal-header">
                                    <div class="terminal-dots">
                                        <div class="dot dot-red"></div>
                                        <div class="dot dot-yellow"></div>
                                        <div class="dot dot-green"></div>
                                    </div>
                                    <div class="terminal-title">boss@archlinux: ~/studentdb/feed</div>
                                    <div class="terminal-status">
                                        <span class="blink-circle"></span>
                                        LIVE
                                    </div>
                                </div>

                                <div class="terminal-body" id="terminalFeed">
                                    <div class="prompt-line">
                                        [boss@archlinux <span class="path">studentdb</span>]$ <span class="cmd">watch -n 1 --color "jdbc tail -f"</span>
                                    </div>
                                    <div style="color: #64748b; font-size: 0.74rem;">[INFO] Connected to MySQL via StudentDAO (Pool: Active)</div>
                                    
                                    <!-- Dynamic entries populated via JS -->
                                    <div id="recentRecordsList" style="display: flex; flex-direction: column; gap: 0.65rem; margin-top: 0.35rem;"></div>

                                    <div style="margin-top: auto; padding-top: 0.5rem; color: #64748b; font-size: 0.75rem;">
                                        <span style="color: var(--green);">Ready</span> - Waiting for next bean transaction<span class="terminal-cursor"></span>
                                    </div>
                                </div>
                            </div>

                        </div>
                    </section>

                    <!-- SECTION 2: DIRECTORY -->
                    <section id="secDirectory" class="view-section">
                        <div class="stats-row">
                            <div class="glass-card stat-card">
                                <div class="stat-icon stat-icon-cyan">&#128101;</div>
                                <div class="stat-info">
                                    <div class="stat-lbl">Total Enrolled</div>
                                    <div id="valTotal" class="stat-num">0</div>
                                </div>
                            </div>
                            <div class="glass-card stat-card">
                                <div class="stat-icon stat-icon-purple">&#9733;</div>
                                <div class="stat-info">
                                    <div class="stat-lbl">Highest GPA</div>
                                    <div id="valTopGpa" class="stat-num">0.00</div>
                                </div>
                            </div>
                        </div>

                        <div class="dir-toolbar">
                            <div>
                                <h2 style="font-size: 1.35rem; font-weight: 800;">Enrolled Students</h2>
                                <p style="font-size: 0.85rem; color: var(--text-muted); margin-top: 0.2rem;">Live query via StudentDAO.getAllStudents()</p>
                            </div>
                            <div class="search-box">
                                <span class="search-icon">&#128269;</span>
                                <input type="text" id="txtSearch" class="modern-input" placeholder="Search name, email, course..." oninput="doSearch()" style="padding-left: 2.4rem;">
                            </div>
                        </div>

                        <div class="glass-card table-card">
                            <table>
                                <thead>
                                    <tr>
                                        <th style="width: 80px;">ID</th>
                                        <th>Student Details</th>
                                        <th>Course</th>
                                        <th>GPA</th>
                                        <th style="text-align: right;">Actions</th>
                                    </tr>
                                </thead>
                                <tbody id="tableBody">
                                    <tr><td colspan="5" style="text-align:center; padding: 2.5rem; color: var(--text-muted);">Loading students...</td></tr>
                                </tbody>
                            </table>
                        </div>
                    </section>
                </main>

                <!-- GLASS UPDATE MODAL -->
                <div id="modalEdit" class="modal-overlay">
                    <div class="glass-card modal-card">
                        <h2 style="font-size: 1.3rem; font-weight: 800; margin-bottom: 0.3rem;">Modify Student Record</h2>
                        <p style="font-size: 0.85rem; color: var(--text-muted); margin-bottom: 1.4rem;">Updates StudentBean state & executes SQL update</p>
                        
                        <form id="frmEdit" style="display:flex; flex-direction:column; gap:1rem;">
                            <input type="hidden" id="editId">
                            <div class="interactive-field" style="margin-bottom:0;">
                                <input type="text" id="editName" class="modern-input" required placeholder="Full Name" style="padding-left:1rem;">
                            </div>
                            <div class="interactive-field" style="margin-bottom:0;">
                                <input type="email" id="editEmail" class="modern-input" required placeholder="Email Address" style="padding-left:1rem;">
                            </div>
                            <div class="interactive-field" style="margin-bottom:0;">
                                <input type="text" id="editCourse" class="modern-input" required placeholder="Course" style="padding-left:1rem;">
                            </div>
                            <div class="interactive-field" style="margin-bottom:0;">
                                <input type="number" step="0.01" min="0" max="10" id="editGpa" class="modern-input" required placeholder="GPA" style="padding-left:1rem;">
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn-secondary" onclick="closeEditModal()">Cancel</button>
                                <button type="submit" class="btn-neon-action" style="width:auto; padding:0.7rem 1.4rem;">Save Changes</button>
                            </div>
                        </form>
                    </div>
                </div>

                <div id="toastStack"></div>

                <script>
                    let activeList = [];

                    function showToast(msg, isError = false) {
                        const box = document.getElementById('toastStack');
                        const el = document.createElement('div');
                        el.className = 'glass-toast';
                        el.style.borderLeft = isError ? '4px solid var(--danger)' : '4px solid var(--cyan)';
                        el.innerText = msg;
                        box.appendChild(el);
                        setTimeout(() => el.remove(), 3200);
                    }

                    // Render Recent Records inside the Hacker Terminal
                    function renderTerminalFeed(list) {
                        const feed = document.getElementById('recentRecordsList');
                        feed.innerHTML = '';

                        if (list.length === 0) {
                            feed.innerHTML = '<div style="color: #64748b; font-size: 0.8rem; padding: 1rem 0;">[stdout] Database is empty. No commits recorded yet.</div>';
                            return;
                        }

                        // Take top 4 most recent
                        const recent = list.slice(0, 4);
                        recent.forEach((s, idx) => {
                            const isLatest = idx === 0;
                            feed.innerHTML += `
                                <div class="record-entry">
                                    <div class="entry-top">
                                        <span class="entry-id">RECORD #${s.studentId} ${isLatest ? '<span style="color:#22c55e; font-size:0.7rem; font-weight:800;">[LATEST COMMIT]</span>' : ''}</span>
                                        <span class="entry-time">${new Date().toLocaleTimeString()}</span>
                                    </div>
                                    <div class="entry-name">${escape(s.name)}</div>
                                    <div style="font-size:0.75rem; color:#64748b;">&lt;${escape(s.email)}&gt;</div>
                                    <div class="entry-details">
                                        <span>PROGRAM: <span class="badge-term-course">${escape(s.course)}</span></span>
                                        <span>GPA: <span class="badge-term-gpa">${s.gpa.toFixed(2)}</span></span>
                                    </div>
                                </div>
                            `;
                        });
                    }

                    function switchSection(target) {
                        const isRegister = target === 'register';
                        document.getElementById('secRegister').classList.toggle('active', isRegister);
                        document.getElementById('secDirectory').classList.toggle('active', !isRegister);
                        document.getElementById('btnTabRegister').classList.toggle('active', isRegister);
                        document.getElementById('btnTabDirectory').classList.toggle('active', !isRegister);

                        if (!isRegister) {
                            fetchStudents();
                        }
                    }

                    async function fetchStudents() {
                        try {
                            const res = await fetch('/api/students');
                            activeList = await res.json();
                            renderGrid(activeList);
                            renderTerminalFeed(activeList);
                            refreshMetrics(activeList);
                        } catch (err) {
                            showToast('Unable to reach database', true);
                        }
                    }

                    function refreshMetrics(data) {
                        document.getElementById('lblNavCount').innerText = data.length;
                        document.getElementById('valTotal').innerText = data.length;
                        if (data.length > 0) {
                            const max = Math.max(...data.map(s => s.gpa));
                            document.getElementById('valTopGpa').innerText = max.toFixed(2);
                        } else {
                            document.getElementById('valTopGpa').innerText = '0.00';
                        }
                    }

                    function renderGrid(data) {
                        const tbody = document.getElementById('tableBody');
                        tbody.innerHTML = '';

                        if (data.length === 0) {
                            tbody.innerHTML = '<tr><td colspan="5" style="text-align:center; padding: 2.5rem; color: var(--text-muted);">No student records found.</td></tr>';
                            return;
                        }

                        data.forEach(s => {
                            tbody.innerHTML += `
                                <tr>
                                    <td><span class="id-tag">#${s.studentId}</span></td>
                                    <td>
                                        <div class="student-cell">
                                            <span class="student-name">${escape(s.name)}</span>
                                            <span class="student-email">${escape(s.email)}</span>
                                        </div>
                                    </td>
                                    <td><span class="course-badge">${escape(s.course)}</span></td>
                                    <td><span class="gpa-tag">${s.gpa.toFixed(2)}</span></td>
                                    <td>
                                        <div class="actions-cell">
                                            <button class="action-btn btn-edit" onclick="openEditModal(${s.studentId})">Edit</button>
                                            <button class="action-btn btn-del" onclick="removeStudent(${s.studentId})">Delete</button>
                                        </div>
                                    </td>
                                </tr>
                            `;
                        });
                    }

                    function doSearch() {
                        const q = document.getElementById('txtSearch').value.toLowerCase().trim();
                        const filtered = activeList.filter(s =>
                            s.name.toLowerCase().includes(q) ||
                            s.email.toLowerCase().includes(q) ||
                            s.course.toLowerCase().includes(q)
                        );
                        renderGrid(filtered);
                    }

                    function escape(text) {
                        return text.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;");
                    }

                    // Register
                    document.getElementById('frmRegister').addEventListener('submit', async (e) => {
                        e.preventDefault();
                        const payload = {
                            name: document.getElementById('regName').value.trim(),
                            email: document.getElementById('regEmail').value.trim(),
                            course: document.getElementById('regCourse').value.trim(),
                            gpa: parseFloat(document.getElementById('regGpa').value)
                        };

                        try {
                            const res = await fetch('/api/students', {
                                method: 'POST',
                                headers: { 'Content-Type': 'application/json' },
                                body: JSON.stringify(payload)
                            });
                            const body = await res.json();

                            if (!res.ok || body.error) {
                                showToast(body.error || 'Failed to enroll student', true);
                                return;
                            }

                            showToast('Student successfully enrolled!');
                            document.getElementById('frmRegister').reset();
                            fetchStudents();
                        } catch (err) {
                            showToast('Server communication failure', true);
                        }
                    });

                    // Edit
                    function openEditModal(id) {
                        const s = activeList.find(x => x.studentId === id);
                        if (!s) return;
                        document.getElementById('editId').value = s.studentId;
                        document.getElementById('editName').value = s.name;
                        document.getElementById('editEmail').value = s.email;
                        document.getElementById('editCourse').value = s.course;
                        document.getElementById('editGpa').value = s.gpa;
                        document.getElementById('modalEdit').classList.add('open');
                    }

                    function closeEditModal() {
                        document.getElementById('modalEdit').classList.remove('open');
                    }

                    document.getElementById('frmEdit').addEventListener('submit', async (e) => {
                        e.preventDefault();
                        const payload = {
                            studentId: parseInt(document.getElementById('editId').value),
                            name: document.getElementById('editName').value.trim(),
                            email: document.getElementById('editEmail').value.trim(),
                            course: document.getElementById('editCourse').value.trim(),
                            gpa: parseFloat(document.getElementById('editGpa').value)
                        };

                        try {
                            const res = await fetch('/api/students', {
                                method: 'PUT',
                                headers: { 'Content-Type': 'application/json' },
                                body: JSON.stringify(payload)
                            });
                            const body = await res.json();

                            if (!res.ok || body.error) {
                                showToast(body.error || 'Update failed', true);
                                return;
                            }

                            showToast('Record updated successfully!');
                            closeEditModal();
                            fetchStudents();
                        } catch (err) {
                            showToast('Network error during update', true);
                        }
                    });

                    // Delete
                    async function removeStudent(id) {
                        if (!confirm('Confirm deletion of student #' + id + '?')) return;
                        try {
                            const res = await fetch('/api/students?id=' + id, { method: 'DELETE' });
                            const body = await res.json();
                            if (body.success) {
                                showToast('Record successfully deleted');
                                fetchStudents();
                            } else {
                                showToast('Failed to delete student', true);
                            }
                        } catch (err) {
                            showToast('Server error while deleting', true);
                        }
                    }

                    fetchStudents();
                </script>
                </body>
                </html>
                """;

            byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }

    static class ApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();

            if ("GET".equalsIgnoreCase(method)) {
                List<StudentBean> list = dao.getAllStudents();
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < list.size(); i++) {
                    StudentBean s = list.get(i);
                    json.append(String.format(
                        "{\"studentId\":%d,\"name\":\"%s\",\"email\":\"%s\",\"course\":\"%s\",\"gpa\":%.2f}",
                        s.getStudentId(), s.getName(), s.getEmail(), s.getCourse(), s.getGpa()
                    ));
                    if (i < list.size() - 1) json.append(",");
                }
                json.append("]");

                byte[] resBytes = json.toString().getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, resBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(resBytes);
                }

            } else if ("POST".equalsIgnoreCase(method)) {
                handleSaveOrUpdate(exchange, false);

            } else if ("PUT".equalsIgnoreCase(method)) {
                handleSaveOrUpdate(exchange, true);

            } else if ("DELETE".equalsIgnoreCase(method)) {
                String query = exchange.getRequestURI().getQuery();
                int id = Integer.parseInt(query.split("=")[1]);
                boolean success = dao.deleteStudent(id);

                String res = "{\"success\":" + success + "}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(success ? 200 : 500, res.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(res.getBytes());
                }
            }
        }

        private void handleSaveOrUpdate(HttpExchange exchange, boolean isUpdate) throws IOException {
            try {
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

                String name = extract(body, "name");
                String email = extract(body, "email");
                String course = extract(body, "course");
                double gpa = Double.parseDouble(extract(body, "gpa"));

                StudentBean bean = new StudentBean(name, email, course, gpa);

                boolean success;
                if (isUpdate) {
                    int studentId = Integer.parseInt(extract(body, "studentId"));
                    bean.setStudentId(studentId);
                    success = dao.updateStudent(bean);
                } else {
                    success = dao.addStudent(bean);
                }

                if (!success) {
                    throw new RuntimeException("Database operation failed. Verify uniqueness and connection.");
                }

                String res = "{\"success\":true}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, res.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(res.getBytes());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                String err = "{\"error\":\"" + ex.getMessage().replace("\"", "'") + "\"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(500, err.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(err.getBytes());
                }
            }
        }

        private String extract(String json, String key) {
            String pattern = "\"" + key + "\":\"";
            int start = json.indexOf(pattern);
            if (start != -1) {
                start += pattern.length();
                int end = json.indexOf("\"", start);
                return json.substring(start, end);
            }
            pattern = "\"" + key + "\":";
            start = json.indexOf(pattern);
            if (start != -1) {
                start += pattern.length();
                int end = json.indexOf(",", start);
                if (end == -1) end = json.indexOf("}", start);
                return json.substring(start, end).trim();
            }
            return "";
        }
    }
}