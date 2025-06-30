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

        // Verifica se está logado
        if (sessao == null || sessao.getAttribute("idUsuario") == null) {
            response.sendRedirect("login");
            return;
        }

        // Verifica se a ação é logout
        String acao = request.getParameter("acao");
        if ("logout".equals(acao)) {
            sessao.invalidate();
            response.sendRedirect("menu");
            return;
        }

        // Caso contrário, mostra o painel
        String nome = (String) sessao.getAttribute("nomeUsuario");

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>Painel</title></head><body>");
        out.println("<h1>Bem-vindo, " + nome + "</h1>");
        out.println("<h3>Escolha uma operação:</h3>");
        out.println("<ul>");
        out.println("<li><a href='deposito'>Depositar</a></li>");
        out.println("<li><a href='saque'>Sacar</a></li>");
        out.println("<li><a href='transferencia'>Transferir</a></li>");
        out.println("<li><a href='investimento'>Investir</a></li>");
        out.println("<li><a href='extrato'>Ver Extrato</a></li>");
        out.println("<li><a href='saldo'>Ver Saldo</a></li>");
        out.println("<li><a href='painel?acao=logout'>Sair</a></li>");
        out.println("</ul>");
        out.println("</body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}