package facitec.edu.py.pretasystem_util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.Icon;

/**
 * Iconos dibujados por codigo (vectoriales): no dependen de archivos .png en
 * rutas de tu computadora, asi que funcionan en cualquier PC y a cualquier tamaño.
 * Tipos: ovillo, orden, caja, etiqueta, camion, usuario, grafico, consumo
 */
public class Iconos {

	public static Icon crear(final String tipo, final int size, final Color color) {
		return new Icon() {
			@Override
			public int getIconWidth() {
				return size;
			}

			@Override
			public int getIconHeight() {
				return size;
			}

			@Override
			public void paintIcon(Component c, Graphics g, int x, int y) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
				g2.translate(x, y);
				g2.scale(size / 48.0, size / 48.0); // se dibuja en una grilla de 48x48
				g2.setColor(color);
				g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				dibujar(g2, tipo);
				g2.dispose();
			}
		};
	}

	public static Image imagen(String tipo, int size, Color color) {
		BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		crear(tipo, size, color).paintIcon(null, g, 0, 0);
		g.dispose();
		return img;
	}

	private static void dibujar(Graphics2D g, String tipo) {
		switch (tipo) {
		case "ovillo": // materia prima
			g.drawOval(7, 7, 34, 34);
			g.drawArc(7, 17, 34, 16, 180, 180);
			g.drawArc(17, 7, 14, 34, 270, 180);
			g.drawLine(36, 37, 44, 42);
			break;
		case "orden": // orden de produccion (tablero con lista)
			g.drawRoundRect(9, 8, 30, 36, 6, 6);
			g.drawRoundRect(17, 4, 14, 8, 4, 4);
			g.drawLine(15, 22, 33, 22);
			g.drawLine(15, 29, 33, 29);
			g.drawLine(15, 36, 26, 36);
			break;
		case "caja": // producto terminado (cubo)
			g.drawPolygon(new Polygon(new int[] { 24, 42, 24, 6 }, new int[] { 6, 15, 24, 15 }, 4));
			g.drawPolygon(new Polygon(new int[] { 6, 24, 24, 6 }, new int[] { 15, 24, 43, 34 }, 4));
			g.drawPolygon(new Polygon(new int[] { 24, 42, 42, 24 }, new int[] { 24, 15, 34, 43 }, 4));
			break;
		case "etiqueta": // categoria
			g.drawPolygon(new Polygon(new int[] { 6, 28, 42, 28, 6 }, new int[] { 10, 10, 24, 38, 38 }, 5));
			g.drawOval(11, 20, 8, 8);
			break;
		case "camion": // proveedor
			g.drawRect(4, 12, 26, 22);
			g.drawPolygon(new Polygon(new int[] { 30, 40, 46, 46, 30 }, new int[] { 18, 18, 26, 34, 34 }, 5));
			g.drawOval(10, 32, 9, 9);
			g.drawOval(34, 32, 9, 9);
			break;
		case "usuario":
			g.drawOval(16, 6, 16, 16);
			g.drawArc(8, 26, 32, 32, 0, 180);
			g.drawLine(8, 42, 40, 42);
			break;
		case "grafico": // reportes
			g.drawLine(6, 41, 43, 41);
			g.drawRect(11, 24, 7, 17);
			g.drawRect(22, 14, 7, 27);
			g.drawRect(33, 7, 7, 34);
			break;
		case "consumo":
			g.drawOval(5, 5, 38, 38);
			g.drawLine(24, 13, 24, 33);
			g.drawLine(24, 33, 17, 26);
			g.drawLine(24, 33, 31, 26);
			break;
		default:
			g.drawOval(8, 8, 32, 32);
		}
	}
}