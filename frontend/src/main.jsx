import React, { useEffect, useMemo, useState } from "react";
import { createRoot } from "react-dom/client";
import {
  BookOpen,
  ChevronLeft,
  ChevronRight,
  GraduationCap,
  LogOut,
  Pencil,
  Plus,
  Save,
  Search,
  Trash2,
  UserRound,
} from "lucide-react";
import { login, request } from "./api";
import "./styles.css";

const emptyStudent = {
  name: "",
  rollNumber: "",
  email: "",
  className: "",
  password: "",
};

function App() {
  const [session, setSession] = useState(() => {
    const token = localStorage.getItem("token");
    const role = localStorage.getItem("role");
    const username = localStorage.getItem("username");
    return token && role && username ? { token, role, username } : null;
  });

  const handleLogin = (auth) => {
    localStorage.setItem("token", auth.token);
    localStorage.setItem("role", auth.role);
    localStorage.setItem("username", auth.username);
    setSession(auth);
  };

  const logout = () => {
    localStorage.clear();
    setSession(null);
  };

  if (!session) {
    return <LoginPage onLogin={handleLogin} />;
  }

  return (
    <Shell session={session} onLogout={logout}>
      {session.role === "TEACHER" ? <TeacherDashboard /> : <StudentDashboard />}
    </Shell>
  );
}

function Shell({ session, onLogout, children }) {
  return (
    <main className="app-shell">
      <aside className="sidebar">
        <div className="brand">
          <GraduationCap size={28} />
          <div>
            <strong>Marks Portal</strong>
            <span>{session.role === "TEACHER" ? "Teacher workspace" : "Student view"}</span>
          </div>
        </div>
        <div className="profile">
          <UserRound size={22} />
          <div>
            <span>{session.username}</span>
            <small>{session.role.toLowerCase()}</small>
          </div>
        </div>
        <button className="ghost-button" onClick={onLogout}>
          <LogOut size={18} /> Logout
        </button>
      </aside>
      <section className="content">{children}</section>
    </main>
  );
}

function LoginPage({ onLogin }) {
  const [form, setForm] = useState({ username: "teacher", password: "teacher123" });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const submit = async (event) => {
    event.preventDefault();
    setLoading(true);
    setError("");
    try {
      onLogin(await login(form.username.trim(), form.password));
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="login-page">
      <section className="login-panel">
        <div className="login-copy">
          <GraduationCap size={48} />
          <h1>Marks Portal</h1>
          <p>Teacher-managed student records with secure student mark access.</p>
          <div className="demo-grid">
            <span>Teacher: teacher / teacher123</span>
            <span>Student: R001 / student123</span>
          </div>
        </div>
        <form className="login-form" onSubmit={submit}>
          <label>
            Username or roll number
            <input
              value={form.username}
              onChange={(event) => setForm({ ...form, username: event.target.value })}
              autoComplete="username"
              required
            />
          </label>
          <label>
            Password
            <input
              type="password"
              value={form.password}
              onChange={(event) => setForm({ ...form, password: event.target.value })}
              autoComplete="current-password"
              required
            />
          </label>
          {error && <div className="error">{error}</div>}
          <button className="primary-button" disabled={loading}>
            {loading ? "Signing in..." : "Login"}
          </button>
        </form>
      </section>
    </main>
  );
}

function TeacherDashboard() {
  const [page, setPage] = useState(0);
  const [students, setStudents] = useState(null);
  const [selected, setSelected] = useState(null);
  const [form, setForm] = useState(emptyStudent);
  const [marks, setMarks] = useState(null);
  const [markForm, setMarkForm] = useState({ subject: "", score: "" });
  const [editingMarkId, setEditingMarkId] = useState(null);
  const [query, setQuery] = useState("");
  const [error, setError] = useState("");

  const filteredStudents = useMemo(() => {
    if (!students?.content) return [];
    const needle = query.trim().toLowerCase();
    if (!needle) return students.content;
    return students.content.filter((student) =>
      `${student.name} ${student.rollNumber} ${student.className}`.toLowerCase().includes(needle)
    );
  }, [students, query]);

  const loadStudents = async () => {
    const data = await request(`/teacher/students?page=${page}&size=5&sort=rollNumber`);
    setStudents(data);
    if (!selected && data.content.length) {
      setSelected(data.content[0]);
    }
  };

  useEffect(() => {
    loadStudents().catch((err) => setError(err.message));
  }, [page]);

  useEffect(() => {
    if (!selected) {
      setMarks(null);
      return;
    }
    setForm({ ...selected, password: "" });
    request(`/teacher/students/${selected.rollNumber}/marks`)
      .then(setMarks)
      .catch((err) => setError(err.message));
  }, [selected]);

  const saveStudent = async (event) => {
    event.preventDefault();
    setError("");
    const body = {
      name: form.name,
      rollNumber: form.rollNumber,
      email: form.email,
      className: form.className,
      password: form.password,
    };
    try {
      const saved = selected?.id
        ? await request(`/teacher/students/${selected.id}`, { method: "PUT", body: JSON.stringify(body) })
        : await request("/teacher/students", { method: "POST", body: JSON.stringify(body) });
      await loadStudents();
      setSelected(saved);
    } catch (err) {
      setError(err.message);
    }
  };

  const deleteStudent = async () => {
    if (!selected?.id) return;
    await request(`/teacher/students/${selected.id}`, { method: "DELETE" });
    setSelected(null);
    setForm(emptyStudent);
    await loadStudents();
  };

  const saveMark = async (event) => {
    event.preventDefault();
    if (!selected?.id) return;
    const body = { subject: markForm.subject, score: Number(markForm.score) };
    const path = editingMarkId ? `/teacher/marks/${editingMarkId}` : `/teacher/students/${selected.id}/marks`;
    const method = editingMarkId ? "PUT" : "POST";
    await request(path, { method, body: JSON.stringify(body) });
    setMarkForm({ subject: "", score: "" });
    setEditingMarkId(null);
    setMarks(await request(`/teacher/students/${selected.rollNumber}/marks`));
  };

  const editMark = (mark) => {
    setEditingMarkId(mark.id);
    setMarkForm({ subject: mark.subject, score: String(mark.score) });
  };

  const deleteMark = async (markId) => {
    await request(`/teacher/marks/${markId}`, { method: "DELETE" });
    setMarks(await request(`/teacher/students/${selected.rollNumber}/marks`));
  };

  return (
    <>
      <header className="page-header">
        <div>
          <span className="eyebrow">Teacher dashboard</span>
          <h2>Students and marks</h2>
        </div>
        <button className="primary-button" onClick={() => { setSelected(null); setForm(emptyStudent); setMarks(null); }}>
          <Plus size={18} /> New student
        </button>
      </header>

      {error && <div className="error">{error}</div>}

      <div className="teacher-grid">
        <section className="panel">
          <div className="panel-tools">
            <div className="search">
              <Search size={16} />
              <input value={query} onChange={(event) => setQuery(event.target.value)} placeholder="Search students" />
            </div>
          </div>
          <div className="student-list">
            {filteredStudents.map((student) => (
              <button
                key={student.id}
                className={`student-row ${selected?.id === student.id ? "active" : ""}`}
                onClick={() => setSelected(student)}
              >
                <strong>{student.name}</strong>
                <span>{student.rollNumber} · {student.className || "No class"}</span>
              </button>
            ))}
          </div>
          <div className="pagination">
            <button disabled={!students || students.first} onClick={() => setPage((value) => value - 1)}>
              <ChevronLeft size={16} />
            </button>
            <span>Page {(students?.number ?? 0) + 1} of {students?.totalPages || 1}</span>
            <button disabled={!students || students.last} onClick={() => setPage((value) => value + 1)}>
              <ChevronRight size={16} />
            </button>
          </div>
        </section>

        <section className="panel">
          <h3>{selected?.id ? "Edit student" : "Create student"}</h3>
          <form className="form-grid" onSubmit={saveStudent}>
            <label>Name<input value={form.name} onChange={(event) => setForm({ ...form, name: event.target.value })} required /></label>
            <label>Roll number<input value={form.rollNumber} onChange={(event) => setForm({ ...form, rollNumber: event.target.value })} required /></label>
            <label>Email<input value={form.email || ""} onChange={(event) => setForm({ ...form, email: event.target.value })} /></label>
            <label>Class<input value={form.className || ""} onChange={(event) => setForm({ ...form, className: event.target.value })} /></label>
            <label>Password<input type="password" value={form.password || ""} onChange={(event) => setForm({ ...form, password: event.target.value })} placeholder={selected?.id ? "Leave blank to keep" : "Defaults to roll number"} /></label>
            <div className="button-row">
              <button className="primary-button"><Save size={18} /> Save</button>
              {selected?.id && <button type="button" className="danger-button" onClick={deleteStudent}><Trash2 size={18} /> Delete</button>}
            </div>
          </form>
        </section>

        <section className="panel marks-panel">
          <h3>{marks?.student?.name || "Select a student"}</h3>
          {marks && <AverageBadge average={marks.average} />}
          <div className="marks-list">
            {marks?.marks?.map((mark) => (
              <div className="mark-row" key={mark.id}>
                <div>
                  <strong>{mark.subject}</strong>
                  <span>{mark.score}/100</span>
                </div>
                <button onClick={() => editMark(mark)} title="Edit mark"><Pencil size={16} /></button>
                <button onClick={() => deleteMark(mark.id)} title="Delete mark"><Trash2 size={16} /></button>
              </div>
            ))}
          </div>
          <form className="mark-form" onSubmit={saveMark}>
            <input value={markForm.subject} onChange={(event) => setMarkForm({ ...markForm, subject: event.target.value })} placeholder="Subject" required />
            <input type="number" min="0" max="100" value={markForm.score} onChange={(event) => setMarkForm({ ...markForm, score: event.target.value })} placeholder="Score" required />
            <button className="primary-button" disabled={!selected?.id}>{editingMarkId ? "Update" : "Add"}</button>
          </form>
        </section>
      </div>
    </>
  );
}

function StudentDashboard() {
  const [data, setData] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    request("/student/me/marks").then(setData).catch((err) => setError(err.message));
  }, []);

  return (
    <>
      <header className="page-header">
        <div>
          <span className="eyebrow">Student dashboard</span>
          <h2>{data?.student?.name || "My marks"}</h2>
        </div>
        {data && <AverageBadge average={data.average} />}
      </header>
      {error && <div className="error">{error}</div>}
      <section className="student-marks-grid">
        {data?.marks?.map((mark) => (
          <article className="subject-card" key={mark.id}>
            <BookOpen size={22} />
            <span>{mark.subject}</span>
            <strong>{mark.score}</strong>
          </article>
        ))}
      </section>
    </>
  );
}

function AverageBadge({ average }) {
  return (
    <div className="average">
      <span>Average</span>
      <strong>{average.toFixed(1)}%</strong>
    </div>
  );
}

createRoot(document.getElementById("root")).render(<App />);
