package com.mycompany.a;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet(name = "painel", urlPatterns = {"/painel"})
public class painel extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sessao = request.getSession(false);

        if (sessao == null || sessao.getAttribute("idUsuario") == null) {
            response.sendRedirect("login");
            return;
        }

        String acao = request.getParameter("acao");
        if ("logout".equals(acao)) {
            sessao.invalidate();
            response.sendRedirect("menu");
            return;
        }

        String nome = (String) sessao.getAttribute("nomeUsuario");

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html><html lang='pt-br'><head>");
        out.println("<meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Painel – Banco Campos</title>");
        out.println("<style>");
        out.println(":root{--bg:#0b1c2c;--accent:#d4af37;--text:#fff;--muted:#cfd6e1}");
        out.println("*{box-sizing:border-box;margin:0;padding:0;font-family:'Segoe UI',Arial,sans-serif}");
        out.println("body{background:var(--bg);color:var(--text);min-height:100vh;display:flex;flex-direction:column}");
        out.println("header{display:flex;justify-content:space-between;align-items:center;padding:20px 8%;background:rgba(0,0,0,.3)}");
        out.println(".logo{font-size:1.8rem;font-weight:700;color:var(--accent)}");
        out.println(".btn{background:var(--accent);color:#000;padding:10px 20px;border:none;border-radius:6px;cursor:pointer;font-weight:600;transition:.3s}");
        out.println(".btn:hover{background:#b48f2e;transform:translateY(-2px)}");
        out.println("main{padding:40px 8%;flex:1}");
        out.println("h1{margin-bottom:10px}");
        out.println("h3{margin-bottom:30px;color:var(--muted)}");
        out.println(".grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(200px,1fr));gap:20px}");
        out.println(".card{background:#13273a;border-left:6px solid var(--accent);padding:24px 20px;border-radius:8px;transition:.3s;cursor:pointer;text-decoration:none;color:var(--text)}");
        out.println(".card:hover{transform:scale(1.03);background:#1b334d}");
        out.println(".card span{font-size:2rem;display:block;margin-bottom:8px}");
        out.println(".logout{text-align:right;margin-top:30px}");
        out.println("</style>");
        out.println("</head><body>");

        // Header
        out.println("<header>");
        out.println("<div class='logo'>Banco Campos</div>");
        out.println("<form method='get' action='painel'><button class='btn' name='acao' value='logout'>Logout</button></form>");
        out.println("</header>");

        // Conteúdo principal
        out.println("<main>");
        out.println("<h1>Bem-vindo, " + nome + " 👋</h1>");
        out.println("<h3>Escolha uma operação:</h3>");

        out.println("<div class='grid'>");

        out.println("<a href='deposito' class='card'><span>➕</span>Depositar</a>");
        out.println("<a href='saque' class='card'><span>➖</span>Sacar</a>");
        out.println("<a href='transferencia' class='card'><span>🔁</span>Transferir</a>");
        out.println("<a href='investimento' class='card'><span>📈</span>Investir</a>");
        out.println("<a href='extrato' class='card'><span>📜</span>Ver Extrato</a>");
        out.println("<a href='saldo' class='card'><span>💰</span>Ver Saldo</a>");

        out.println("</div>");
        out.println("</main>");

        out.println("</body></html>");
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}