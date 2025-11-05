import os

entities = ["FraisBancaire"]

ENTITY_ID_TYPE = "Integer"
DAO_PACKAGE = "itu.banque.compte.daos"
ENTITY_PACKAGE = "itu.banque.compte.entities"
PERSISTENCE_UNIT_NAME = "service-comptePersistenceUnit"
OUTPUT_DIRECTORY = "generated_daos"

DAO_TEMPLATE = """package {PACKAGE_NAME};

import {ENTITY_PACKAGE}.{ENTITY_NAME};
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class {ENTITY_NAME}DAO {{

    @PersistenceContext(unitName = "{PERSISTENCE_UNIT}")
    private EntityManager em;

    public {ENTITY_NAME} create({ENTITY_NAME} {entity_name_lowercase}) {{
        em.persist({entity_name_lowercase});
        return {entity_name_lowercase};
    }}

    public {ENTITY_NAME} findById({ENTITY_ID_TYPE} id) {{
        return em.find({ENTITY_NAME}.class, id);
    }}

    public List<{ENTITY_NAME}> findAll() {{
        return em.createQuery("SELECT e FROM {ENTITY_NAME} e", {ENTITY_NAME}.class).getResultList();
    }}

    public {ENTITY_NAME} update({ENTITY_NAME} {entity_name_lowercase}) {{
        return em.merge({entity_name_lowercase});
    }}

    public void delete({ENTITY_ID_TYPE} id) {{
        {ENTITY_NAME} {entity_name_lowercase} = findById(id);
        if ({entity_name_lowercase} != null) {{
            em.remove({entity_name_lowercase});
        }}
    }}
}}
"""

for entity in entities:
    print("Generating DAO file...")
    content = DAO_TEMPLATE.format(
        ENTITY_NAME=entity,
        entity_name_lowercase=entity[0].lower() + entity[1:],
        ENTITY_ID_TYPE=ENTITY_ID_TYPE,
        PACKAGE_NAME=DAO_PACKAGE,
        ENTITY_PACKAGE=ENTITY_PACKAGE,
        PERSISTENCE_UNIT=PERSISTENCE_UNIT_NAME
    )

    os.makedirs(OUTPUT_DIRECTORY, exist_ok=True)
    file_path = os.path.join(OUTPUT_DIRECTORY, f"{entity}DAO.java")


    try:
        with open(file_path, "w", encoding='utf-8') as f:
            f.write(content)
        print(f"Successfully generated DAO file: {file_path}")
    except IOError as e:
        print(f"Error writing to file {file_path}: {e}")



