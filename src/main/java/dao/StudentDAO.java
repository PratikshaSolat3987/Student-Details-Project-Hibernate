package dao;

import model.Student;
import util.HibernateUtil;
import org.hibernate.Session;

import java.util.List;

public class StudentDAO {

    public void saveStudent(Student student) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(student);
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException("Error saving student: " + e.getMessage());
        }
    }

    public List<Student> getAllStudents() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Student", Student.class).list();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching students: " + e.getMessage());
        }
    }

    public Student getStudent(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Student.class, id);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching student: " + e.getMessage());
        }
    }

    public void updateStudent(Student student) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.merge(student);
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException("Error updating student: " + e.getMessage());
        }
    }

    public void deleteStudent(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Student student = session.get(Student.class, id);
            if (student != null) {
                session.remove(student);
            }
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException("Error deleting student: " + e.getMessage());
        }
    }
}

