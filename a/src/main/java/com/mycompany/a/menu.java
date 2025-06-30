
package com.mycompany.a;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "menu", urlPatterns = {"/menu"})
public class menu extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
            try (PrintWriter out = response.getWriter()) {
                out.println("<!DOCTYPE html>");
                out.println("<html lang='pt-br'>");
                out.println("<head>");
                out.println("<meta charset='UTF-8' />");
                out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0' />");
                out.println("<title>Banco Campos – Inovação e Confiança</title>");
                out.println("<style>");
                out.println("  :root {");
                out.println("    --bg-primary: #0b1c2c;");
                out.println("    --accent: #d4af37;");
                out.println("    --text-light: #ffffff;");
                out.println("    --text-muted: #cfd6e1;");
                out.println("  }");
                out.println("  * { box-sizing: border-box; margin: 0; padding: 0; font-family: 'Segoe UI', Arial, sans-serif; }");
                out.println("  body { background: var(--bg-primary); color: var(--text-light); line-height: 1.6; }");
                out.println("  header { display: flex; align-items: center; justify-content: space-between; padding: 20px 8%; background: rgba(0,0,0,0.3); backdrop-filter: blur(4px); }");
                out.println("  .logo { font-size: 1.8rem; font-weight: 700; color: var(--accent); letter-spacing: 1px; }");
                out.println("  nav a { color: var(--text-light); margin-left: 24px; text-decoration: none; position: relative; }");
                out.println("  nav a::after { content: ''; position: absolute; left: 0; bottom: -4px; width: 0; height: 2px; background: var(--accent); transition: width .3s; }");
                out.println("  nav a:hover::after { width: 100%; }");
                out.println("  .btn { background: var(--accent); color: #000; padding: 10px 20px; border: none; border-radius: 6px; cursor: pointer; margin-left: 12px; font-weight: 600; transition: transform .2s, box-shadow .2s; }");
                out.println("  .btn:hover { transform: translateY(-2px); box-shadow: 0 4px 8px rgba(0,0,0,.4); }");
                out.println("  .hero { text-align: center; padding: 120px 6% 80px; background: linear-gradient(135deg, rgba(212,175,55,0.12) 0%, rgba(11,28,44,0.9) 60%); }");
                out.println("  .hero h1 { font-size: clamp(2.3rem, 6vw, 3.8rem); margin-bottom: 16px; }");
                out.println("  .hero p { font-size: 1.1rem; color: var(--text-muted); max-width: 700px; margin: 0 auto; }");
                out.println("  section { padding: 80px 8%; }");
                out.println("  section h2 { color: var(--accent); font-size: 2rem; margin-bottom: 20px; text-align: center; }");
                out.println("  .sobre p { max-width: 800px; margin: 0 auto; text-align: justify; }");
                out.println("  .clientes-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(240px,1fr)); gap: 24px; margin-top: 40px; }");
                out.println("  .card { background: #13273a; padding: 24px; border-radius: 8px; position: relative; transition: transform .3s, box-shadow .3s; }");
                out.println("  .card:hover { transform: translateY(-6px); box-shadow: 0 10px 18px rgba(0,0,0,.5); }");
                out.println("  .card::before { content: ''; position: absolute; inset: 0; border-radius: 8px; background: linear-gradient(135deg, transparent 60%, var(--accent) 100%); opacity: 0; transition: opacity .4s; }");
                out.println("  .card:hover::before { opacity: 0.12; }");
                out.println("  .card h3 { margin-bottom: 8px; color: var(--accent); }");
                out.println("  .card p { color: var(--text-muted); font-size: .9rem; }");
                out.println("  footer { background: #09141f; padding: 40px 8%; display: grid; gap: 20px; }");
                out.println("  footer h4 { color: var(--accent); margin-bottom: 10px; }");
                out.println("  footer a { color: var(--text-muted); text-decoration: none; }");
                out.println("  footer a:hover { color: var(--accent); }");
                out.println("  @media (max-width: 560px) { nav { display: none; } header { justify-content: space-between; } }");
                out.println("</style>");
                out.println("</head>");
                out.println("<body>");

                // HEADER
                out.println("<header>");
                out.println("<div class='logo'>Banco Campos</div>");
                out.println("<nav>");
                out.println("<a href='#sobre'>Quem Somos</a>");
                out.println("<a href='#clientes'>Clientes</a>");
                out.println("<a href='login'>Login</a>");
                out.println("<button class='btn' onclick=\"location.href='cadastro'\">Sign‑up</button>");
                out.println("</nav>");
                out.println("</header>");

                // HERO
                out.println("<section class='hero'>");
                out.println("<h1>Bem‑vindo ao futuro das finanças</h1>");
                out.println("<p>O Banco Campos combina tecnologia de ponta com atendimento humano para abrir as portas da prosperidade. Controle suas contas, investimentos e conquiste seus objetivos sem complicação.</p>");
                out.println("</section>");

                // QUEM SOMOS
                out.println("<section id='sobre' class='sobre'>");
                out.println("<h2>Quem Somos</h2>");
                out.println("<p>");
                out.println("Fundado em 2025, o <strong>Banco Campos</strong> nasceu da visão de criar uma instituição que unisse <em>inovação</em> e <em>confiança</em>. Em um mundo em constante mudança digital, percebemos que as pessoas precisavam de um parceiro financeiro que fosse tão ágil quanto seguro. Orgulhamo‑nos de oferecer soluções bancárias 100% online, sem abrir mão do calor humano: nossa equipe está sempre pronta para ajudar você a transformar planos em conquistas.");
                out.println("</p>");
                out.println("</section>");

                // CLIENTES
                out.println("<section id='clientes' class='clientes'>");
                out.println("<h2>Clientes</h2>");
                out.println("<div class='clientes-grid'>");

                out.println("<div class='card'>");
                out.println("<h3>Turma de Introdução a Desenvolvimento Web A1</h3>");
                out.println("<p>Parceiros desde 2025, aplicam nossos serviços para projetos acadêmicos que exploram desenvolvimento full‑stack e boas práticas.</p>");
                out.println("</div>");

                out.println("<div class='card'>");
                out.println("<h3>StartUp Kinetix</h3>");
                out.println("<p>Fintech de pagamentos instantâneos que confiou ao Banco Campos a gestão de tesouraria e investimentos de curto prazo.</p>");
                out.println("</div>");

                out.println("<div class='card'>");
                out.println("<h3>Café Aurora</h3>");
                out.println("<p>Rede de cafeterias artesanais que usa nossas APIs para conciliar vendas e fluxo de caixa em tempo real.</p>");
                out.println("</div>");

                out.println("<div class='card'>");
                out.println("<h3>Associação Veleiros do Atlântico</h3>");
                out.println("<p>Organização esportiva que encontrou no Banco Campos o suporte perfeito para patrocínios e eventos internacionais.</p>");
                out.println("</div>");

                out.println("</div>");
                out.println("</section>");

                // FOOTER
                out.println("<footer>");
                out.println("<div>");
                out.println("<h4>Contato</h4>");
                out.println("<p>Tel: (21) 4002‑8922</p>");
                out.println("<p>Email: contato@bancocampos.com</p>");
                out.println("</div>");
                out.println("<div>");
                out.println("<h4>Endereço</h4>");
                out.println("<p>Av. Jornalista Alberto Francisco Torres, Praia de Icaraí, Niterói – RJ, Brasil</p>");
                out.println("</div>");
                out.println("<div>");
                out.println("<h4>Links Rápidos</h4>");
                out.println("<a href='#sobre'>Quem Somos</a><br />");
                out.println("<a href='#clientes'>Clientes</a><br />");
                out.println("<a href='menu'>Menu Inicial</a>");
                out.println("</div>");
                out.println("</footer>");

                out.println("</body>");
                out.println("</html>");
            }
        }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Menu principal do sistema bancário";
    }
}