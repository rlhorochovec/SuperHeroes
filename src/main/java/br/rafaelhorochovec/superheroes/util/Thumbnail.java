package br.rafaelhorochovec.superheroes.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * Helper para geração de miniaturas de imagens.
 * 
 * @author Robson J. P.
 * 
 */
public class Thumbnail {

	/**
	 * Transforma o tamanho da imagem proporcionalmente.
	 * 
	 * @param in imagem original
	 * @param out imagem de saída
	 * @param thumbWidth largura
	 * @param thumbHeight altura
	 * @param quality qualidade
	 * @param pb true gera preto e branco, false gera colorida
	 * @throws IOException
	 * 
	 */
	public static void gerarProprocional(InputStream in, OutputStream out,
			int thumbWidth, int thumbHeight, int quality, boolean pb)
			throws IOException {
		generateThumb(ImageIO.read(in), out, thumbWidth, thumbHeight, quality, pb);
	}

	/**
	 * Transforma o tamanho da imagem para o tamanho exato passado.
	 * 
	 * @param in
	 * @param out
	 * @param thumbWidth
	 * @param thumbHeight
	 * @param quality
	 * @param pb
	 * @throws IOException
	 * 
	 */
	public static void gerarTamanhoExato(InputStream in, OutputStream out,
			int thumbWidth, int thumbHeight, int quality, boolean pb)
			throws IOException {
		generateThumbRect(ImageIO.read(in), out, thumbWidth, thumbHeight, quality, pb);
	}

	private static void generateThumb(Image image, OutputStream out,
			int thumbWidth, int thumbHeight, int quality, boolean pb)
			throws IOException {
		Graphics2DImage g2dImage = generateThumbG2D(image, thumbWidth, thumbHeight, pb);
		saveToOutput(g2dImage.getImage(), out, quality);
	}

	private static Graphics2DImage generateThumbG2D(Image image,
			int thumbWidth, int thumbHeight, boolean pb) throws IOException {
		thumbWidth = Math.min(thumbWidth, 1280);
		thumbHeight = Math.min(thumbHeight, 1024);
		int imageWidth = image.getWidth(null);
		int imageHeight = image.getHeight(null);
		int x = 0;
		int y = 0;

		if (thumbHeight == -1) {
			// Gera thumb quadrado.
			thumbHeight = thumbWidth;
			if (imageWidth > imageHeight) {
				x = (imageWidth - imageHeight) / 2;
			} else if (imageHeight > imageWidth) {
				y = (imageHeight - imageWidth) / 2;
			}
		} else {
			double thumbRatio = (double) thumbWidth / (double) thumbHeight;
			double imageRatio = (double) imageWidth / (double) imageHeight;

			if (thumbRatio < imageRatio) {
				thumbHeight = (int) (thumbWidth / imageRatio);
			} else {
				thumbWidth = (int) (thumbHeight * imageRatio);
			}
		}

		// Achando o tipo da imagem de origem.
		int type = BufferedImage.TYPE_INT_RGB;
		if (image instanceof BufferedImage) {
			if (((BufferedImage) image).getTransparency() == Transparency.TRANSLUCENT) {
				type = BufferedImage.TYPE_INT_ARGB;
			}
		}

		// Draw original image to thumbnail image object and scale it to the new size on-the-fly.
		if (pb) {
			type = BufferedImage.TYPE_BYTE_GRAY;
		}

		BufferedImage thumbImage = new BufferedImage(thumbWidth, thumbHeight, type);
		Graphics2D graphics2D = thumbImage.createGraphics();

		if (x > 0 || y > 0) {
			// graphics2D.drawImage(image, 0, 0, thumbWidth, thumbHeight, x, y, imageWidth - x, imageHeight - y, null);

			// Gera a imagem quadrada.
			BufferedImage tmpImage = new BufferedImage(imageWidth - x, imageHeight - y, type);
			tmpImage.createGraphics().drawImage(image, 0, 0, imageWidth - x, imageHeight - y, x, y, imageWidth - x, imageHeight - y, null);
			graphics2D.drawImage(tmpImage.getScaledInstance(thumbWidth, thumbHeight, Image.SCALE_SMOOTH), 0, 0, null);
		} else {
			graphics2D.drawImage(image.getScaledInstance(thumbWidth, thumbHeight, Image.SCALE_SMOOTH), x, y, null);
			// graphics2D.drawImage(image, x, y, thumbWidth, thumbHeight, null);
		}
		return new Graphics2DImage(thumbImage, graphics2D);
	}

	/**
	 * Gera um thumb da imagem com tamanho exato ao passado por parâmetro.
	 * Redimensiona proporcionalmente a imagem para o tamanho desejado.
	 * 
	 * @throws IOException
	 */
	private static Graphics2DImage generateThumbRectG2D(Image image,
			int thumbWidth, int thumbHeight, boolean pb) throws IOException {
		int imageWidth = image.getWidth(null);
		int imageHeight = image.getHeight(null);

		double imageRatio = (double) imageWidth / (double) imageHeight;
		double thumbRatio = (double) thumbWidth / (double) thumbHeight;

		int thumbPropWidth = (int) (thumbHeight * imageRatio);
		int thumbPropHeight = (int) (thumbWidth / imageRatio);

		int tamWidth = imageWidth;
		int tamHeight = imageHeight;

		int x = 0;
		int y = 0;

		// if (thumbWidth > thumbHeight) {
		if (thumbRatio > imageRatio) {
			tamHeight = (int) ((double) imageHeight * ((double) thumbHeight / (double) thumbPropHeight));
			y = (imageHeight - tamHeight) / 2;
			// } else if (thumbHeight > thumbWidth) {
		} else if (thumbRatio < imageRatio) {
			tamWidth = (int) ((double) imageWidth * ((double) thumbWidth / (double) thumbPropWidth));
			x = (imageWidth - tamWidth) / 2;
		}

		// Achando o tipo da imagem de origem.
		int type = BufferedImage.TYPE_INT_RGB;
		if (image instanceof BufferedImage) {
			if (((BufferedImage) image).getTransparency() == Transparency.TRANSLUCENT) {
				type = BufferedImage.TYPE_INT_ARGB;
			}
		}

		// Draw original image to thumbnail image object and scale it to the new size on-the-fly
		BufferedImage thumbImage = null;

		if (pb)
			type = BufferedImage.TYPE_BYTE_GRAY;
		thumbImage = new BufferedImage(tamWidth, tamHeight, type);
		
		Graphics2D graphics2D = thumbImage.createGraphics();
		// graphics2D.drawImage(image, 0, 0, thumbWidth, thumbHeight, x, y, x + tamWidth, y + tamHeight, null);
		graphics2D.drawImage(image, 0, 0, tamWidth, tamHeight, x, y, x + tamWidth, y + tamHeight, null);
		
		Image ii = thumbImage.getScaledInstance(thumbWidth, thumbHeight, Image.SCALE_SMOOTH);
		BufferedImage bu = new BufferedImage(thumbWidth, thumbHeight, type);
		bu.createGraphics().drawImage(ii, 0, 0, null);
		return new Graphics2DImage(bu, graphics2D);
	}

	private static void generateThumbRect(Image image, OutputStream out,
			int thumbWidth, int thumbHeight, int quality, boolean pb)
			throws IOException {
		Graphics2DImage g2dImage = generateThumbRectG2D(image, thumbWidth, thumbHeight, pb);
		saveToOutput(g2dImage.getImage(), out, quality);
	}

	private static void saveToOutput(BufferedImage image, OutputStream out,
			int quality) throws IOException {

		// Transparency.TRANSLUCENT
		if (image.getTransparency() == Transparency.TRANSLUCENT) {
			// Converte para PNG.
			ImageIO.write(image, "PNG", out);
		} else {
			// Converte para JPG.
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
			JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(image);
			quality = Math.max(0, Math.min(quality, 100));
			param.setQuality((float) quality / 100.0f, false);
			encoder.setJPEGEncodeParam(param);
			encoder.encode(image);
			out.close();
		}
	}

	/**
	 * Classe Utilitária com uma imagem e seu respectivo graphics2D.
	 * 
	 * @author Robson J. P.
	 * 
	 */
	@SuppressWarnings("unused")
	private static class Graphics2DImage {
		private BufferedImage image;
		private Graphics2D graphics2D;

		public Graphics2DImage(BufferedImage image, Graphics2D graphics2D) {
			super();
			this.image = image;
			this.graphics2D = graphics2D;
		}

		public Graphics2DImage(Image image) {
			BufferedImage buffDefault = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics2D = buffDefault.createGraphics();
			graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			graphics2D.drawImage(image, 0, 0, image.getWidth(null), image.getHeight(null), null);
			this.image = buffDefault;
			this.graphics2D = graphics2D;
		}

		public BufferedImage getImage() {
			return image;
		}

		public Graphics2D getGraphics2D() {
			return graphics2D;
		}
	}
}