package br.rafaelhorochovec.superheroes.bean;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.primefaces.model.UploadedFile;

import br.rafaelhorochovec.superheroes.dao.DaoGeneric;
import br.rafaelhorochovec.superheroes.dao.UsuarioDao;
import br.rafaelhorochovec.superheroes.dao.impl.UsuarioDaoImpl;
import br.rafaelhorochovec.superheroes.model.Usuario;
import br.rafaelhorochovec.superheroes.util.Thumbnail;

@ViewScoped
@ManagedBean(name = "usuarioBean")
public class UsuarioBean {

	private Usuario usuario = new Usuario();
	private DaoGeneric<Usuario> daoGeneric = new DaoGeneric<Usuario>();
	private List<Usuario> usuarios = new ArrayList<Usuario>();
	
	private UsuarioDao usuarioDao = new UsuarioDaoImpl();
	
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
			getUsuario().setFoto(arquivoFoto.getFileName());
			try {
				// Pega o caminho real no servidor da pasta upload do projeto.
				String caminhoImagens = getRealServerPath("/upload/usuarios");
				// Salva o arquivo na pasta upload do projeto.
				FileOutputStream fout = new FileOutputStream(caminhoImagens + File.separator + arquivoFoto.getFileName());
				// Gera uma documento do tamanho exato 150x150.
				Thumbnail.gerarTamanhoExato(arquivoFoto.getInputstream(), fout, 150, 150, 90, false);
			} catch (Exception e) {
				System.out.println("Ocorreu um erro ao salvar o arquivo do upload!");
				throw new RuntimeException(e);
			}
		}
				
		usuario = daoGeneric.merge(usuario);
		FacesContext ctx = FacesContext.getCurrentInstance();
		ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso,", usuario.getNome() + " salva(o)."));
		listarUsuarios();
		return "";
	}
	
	private String getRealServerPath(String caminhoRelativo) {
		return FacesContext.getCurrentInstance().getExternalContext().getRealPath(caminhoRelativo);
	}
	
	public String novo(){
		usuario = new Usuario();
		return "";
	}
	
	public String excluir(){
		daoGeneric.deletePorId(usuario);
		usuario = new Usuario();
		FacesContext ctx = FacesContext.getCurrentInstance();
		ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso,", usuario.getNome() + " removida(o)."));
		listarUsuarios();
		return "";
	}
	
	@PostConstruct
	public void listarUsuarios(){
		usuarios = daoGeneric.getListEntity(Usuario.class);
	}
	
	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public DaoGeneric<Usuario> getDaoGeneric() {
		return daoGeneric;
	}

	public void setDaoGeneric(DaoGeneric<Usuario> daoGeneric) {
		this.daoGeneric = daoGeneric;
	}
	
	public List<Usuario> getUsuarios() {
		return usuarios;
	}
	
	public String logar(){
		Usuario login = usuarioDao.login(usuario.getUsuario(), usuario.getSenha());
		
		if (login == null) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Acesso Negado!", "Usuário e/ou senha inválidos!"));
			return "login.xhtml";
		} else {
			getSession().put("usuarioLogado", login);
			return "/app/home.xhtml?faces-redirect=true";
		}
	}
	
	public boolean permiteAcesso(String acesso) {
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = context.getExternalContext();
		Usuario usuarioPerfil = (Usuario) externalContext.getSessionMap().get("usuarioLogado");
		return usuarioPerfil.getPerfil().equals(acesso);
	}
	
	public String sair() {
		getSession().remove("usuarioLogado");
		return "/login.xhtml?faces-redirect=true";
	}
	
	public Usuario getUsuarioLogado() {
		return (Usuario) getSession().get("usuarioLogado");
	}

	/**
	 * Retorna um map que representa as variáveis guardadas a sessăo
	 */
	protected Map<String, Object> getSession() {
		return FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
	}
	
	public String getUrlPTBR() {
		return "//cdn.datatables.net/plug-ins/1.10.12/i18n/Portuguese-Brasil.json";
	}
}
