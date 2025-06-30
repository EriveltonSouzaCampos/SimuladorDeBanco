
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
            out.println("<html><head><title>Bem-vindo</title></head><body>");
            out.println("<h1>Sistema Bancário</h1>");
            out.println("<h3>Escolha uma opção:</h3>");
            out.println("<ul>");
            out.println("<li><a href='cadastro'>Cadastrar novo usuário</a></li>");
            out.println("<li><a href='login'>Fazer login</a></li>");
            out.println("</ul>");
            out.println("</body></html>");
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