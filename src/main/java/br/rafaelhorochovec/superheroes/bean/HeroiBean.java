package br.rafaelhorochovec.superheroes.bean;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.UploadedFile;

import br.rafaelhorochovec.superheroes.dao.DaoGeneric;
import br.rafaelhorochovec.superheroes.model.Heroi;
import br.rafaelhorochovec.superheroes.util.Thumbnail;

@ViewScoped
@ManagedBean(name = "heroiBean")
public class HeroiBean {

	private Heroi heroi = new Heroi();
	private DaoGeneric<Heroi> daoGeneric = new DaoGeneric<Heroi>();
	private List<Heroi> herois = new ArrayList<Heroi>();
	
	// Upload da foto.
	private UploadedFile arquivoFoto;

	public Object getArquivoFoto() {
		return arquivoFoto;
	}

	public void setArquivoFoto(Object obj) {
		if (obj instanceof UploadedFile) {
			this.arquivoFoto = (UploadedFile) obj;
		} else {
			this.arquivoFoto = null;
		}
	}

	public String salvar() throws IOException {
		// Método que verifica para realizar o upload.
		if (arquivoFoto.getFileName() != null) {
			// Grava na propriedade foto o nome do arquivo.
			getHeroi().setFoto(arquivoFoto.getFileName());
			try {
				// Pega o caminho real no servidor da pasta upload do projeto.
				String caminhoImagens = getRealServerPath("/upload/herois");
				// Salva o arquivo na pasta upload do projeto.
				FileOutputStream fout = new FileOutputStream(caminhoImagens + File.separator + arquivoFoto.getFileName());
				// Gera uma documento do tamanho exato 150x150.
				Thumbnail.gerarTamanhoExato(arquivoFoto.getInputstream(), fout, 150, 150, 90, false);
			} catch (Exception e) {
				System.out.println("Ocorreu um erro ao salvar o arquivo do upload!");
				throw new RuntimeException(e);
			}
		}
		
		heroi = daoGeneric.merge(heroi);
		FacesContext ctx = FacesContext.getCurrentInstance();
		ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso,", heroi.getNomeHeroi() + " salva(o)."));
		listarHerois();
		return "";
	}
	
	private String getRealServerPath(String caminhoRelativo) {
		return FacesContext.getCurrentInstance().getExternalContext().getRealPath(caminhoRelativo);
	}
	
	public String novo(){
		heroi = new Heroi();
		return "";
	}
	
	public String excluir(){
		daoGeneric.deletePorId(heroi);
		heroi = new Heroi();
		FacesContext ctx = FacesContext.getCurrentInstance();
		ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso,", heroi.getNomeHeroi() + " removida(o)."));
		listarHerois();
		return "";
	}
	
	@PostConstruct
	public void listarHerois(){
		herois = daoGeneric.getListEntity(Heroi.class);
	}
	
	public Heroi getHeroi() {
		return heroi;
	}

	public void setHeroi(Heroi heroi) {
		this.heroi = heroi;
	}

	public DaoGeneric<Heroi> getDaoGeneric() {
		return daoGeneric;
	}

	public void setDaoGeneric(DaoGeneric<Heroi> daoGeneric) {
		this.daoGeneric = daoGeneric;
	}
	
	public List<Heroi> getHerois() {
		return herois;
	}
	
	public String getUrlPTBR() {
		return "//cdn.datatables.net/plug-ins/1.10.12/i18n/Portuguese-Brasil.json";
	}
}
