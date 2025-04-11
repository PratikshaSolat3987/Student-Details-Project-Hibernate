package controller;

import dao.StudentDAO;
import model.Student;
import model.User;
import util.HibernateUtil;
import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(urlPatterns = {"/students/*"})
public class StudentServlet extends HttpServlet {
    private StudentDAO studentDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        studentDAO = new StudentDAO();
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect("login.html");
            return;
        }

        User user = (User) session.getAttribute("user");
        resp.setContentType("application/json");
        String path = req.getPathInfo() == null ? "/" : req.getPathInfo();
        System.out.println("GET request received for path: " + path);

        try {
            if (path.equals("/")) {
                System.out.println("Fetching all students & user");
                HashMap<String, Object> obj = new HashMap<>();
                obj.put("user", user.getName());
                obj.put("students", studentDAO.getAllStudents());
                resp.getWriter().write(gson.toJson(obj));
            } else if (path.startsWith("/edit/")) {
                String idStr = path.substring(6);
                if (idStr != null && !idStr.trim().isEmpty()) {
                    int id = Integer.parseInt(idStr);
                    Student student = studentDAO.getStudent(id);
                    if (student != null) {
                        resp.getWriter().write(gson.toJson(student));
                    } else {
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        resp.getWriter().write("{\"error\":\"Student not found\"}");
                    }
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write("{\"error\":\"Invalid student ID\"}");
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Invalid endpoint\"}");
            }
        } catch (Exception e) {
            System.out.println("Error in doGet: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            resp.getWriter().write(gson.toJson(error));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String action = req.getParameter("action");
        System.out.println("POST request received with action: " + action);
        Map<String, String> response = new HashMap<>();

        try {
            if ("register".equals(action)) {
                Student student = new Student(
                        req.getParameter("firstName"),
                        req.getParameter("lastName"),
                        req.getParameter("email"),
                        req.getParameter("mobileNumber"),
                        req.getParameter("course"),
                        req.getParameter("grade")
                );
                studentDAO.saveStudent(student);
                response.put("message", "Student registered successfully");

            } else if ("update".equals(action)) {
                String studentIdStr = req.getParameter("studentId");
                if (studentIdStr != null && !studentIdStr.trim().isEmpty()) {
                    int studentId = Integer.parseInt(studentIdStr);
                    Student student = studentDAO.getStudent(studentId);
                    if (student != null) {
                        student.setFirstName(req.getParameter("firstName"));
                        student.setLastName(req.getParameter("lastName"));
                        student.setEmail(req.getParameter("email"));
                        student.setMobileNumber(req.getParameter("mobileNumber"));
                        student.setCourse(req.getParameter("course"));
                        student.setGrade(req.getParameter("grade"));
                        studentDAO.updateStudent(student);
                        response.put("message", "Student updated successfully");
                    } else {
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        response.put("error", "Student not found");
                    }
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.put("error", "Invalid or missing student ID");
                }

            } else if ("delete".equals(action)) {
                String studentIdStr = req.getParameter("studentId");
                if (studentIdStr != null && !studentIdStr.trim().isEmpty()) {
                    int studentId = Integer.parseInt(studentIdStr);
                    studentDAO.deleteStudent(studentId);
                    response.put("message", "Student deleted successfully");
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.put("error", "Invalid or missing student ID");
                }

            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.put("error", "Invalid action");
            }
        } catch (Exception e) {
            System.out.println("Error in doPost: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.put("error", e.getMessage());
        }

        resp.getWriter().write(gson.toJson(response));
    }

    @Override
    public void destroy() {
        HibernateUtil.shutdown();
        System.out.println("Servlet destroyed, SessionFactory closed");
    }
}
