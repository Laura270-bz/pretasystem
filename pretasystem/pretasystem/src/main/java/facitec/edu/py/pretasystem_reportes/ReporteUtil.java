package facitec.edu.py.pretasystem_reportes;

import java.awt.Component;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

public class ReporteUtil {

	// Carpeta dentro de src/main/resources donde viven solo los .jrxml
	private static final String CARPETA = "/reportes/";

	public static void mostrar(Component padre, String archivoJrxml, String titulo, Collection<?> datos,
			Map<String, Object> parametros) {
		try {
			InputStream jrxml = ReporteUtil.class.getResourceAsStream(CARPETA + archivoJrxml);
			if (jrxml == null) {
				throw new IllegalStateException("No se encontró " + CARPETA + archivoJrxml);
			}
			JasperReport reporte = JasperCompileManager.compileReport(jrxml);

			Map<String, Object> params = (parametros == null) ? new HashMap<>() : parametros;
			JasperPrint impresion = JasperFillManager.fillReport(reporte, params,
					new JRBeanCollectionDataSource(datos));

			JasperViewer visor = new JasperViewer(impresion, false);
			visor.setTitle(titulo);
			visor.setVisible(true);

		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(padre, "Error al generar el reporte: " + ex.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}