package com.talentsexpress.api.repository;

import com.talentsexpress.api.model.Servico;
import com.talentsexpress.api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, Long> {

    List<Servico> findByPrestador(Usuario prestador);

    boolean existsByPrestadorAndAtivoTrue(Usuario prestador);

    /**
     * RN02: Só retorna serviços ativos de prestadores com perfil completo.
     * Aplica filtros de texto (título/descrição), categoria e bairro.
     */
    @Query("""
        SELECT s FROM Servico s
        JOIN s.prestador p
        WHERE s.ativo = true
          AND p.ativo = true
          AND (:texto IS NULL OR LOWER(s.titulo) LIKE LOWER(CONCAT('%', :texto, '%'))
               OR LOWER(s.descricao) LIKE LOWER(CONCAT('%', :texto, '%'))
               OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :texto, '%')))
          AND (:categoria IS NULL OR CAST(s.categoria AS string) = :categoria)
          AND (:bairro IS NULL OR LOWER(p.bairro) = LOWER(:bairro))
        ORDER BY s.criadoEm DESC
        """)
    List<Servico> findElegiveis(
            @Param("texto") String texto,
            @Param("categoria") String categoria,
            @Param("bairro") String bairro);
}
