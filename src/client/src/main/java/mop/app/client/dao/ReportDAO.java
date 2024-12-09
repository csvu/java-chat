package mop.app.client.dao;

import java.util.List;
import mop.app.client.dto.ReportDTO;
import mop.app.client.util.HibernateUtil;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReportDAO {
    private static final Logger logger = LoggerFactory.getLogger(ReportDAO.class);

    public ReportDAO() {}

    public List<ReportDTO> getAllReports() {
        List<ReportDTO> reports = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            reports = session.createQuery("FROM ReportDTO ORDER BY createdAt DESC", ReportDTO.class).list();
        } catch (Exception e) {
            logger.error("Failed to get all reports: {}", e.getMessage());
        }

        return reports;
    }
}
