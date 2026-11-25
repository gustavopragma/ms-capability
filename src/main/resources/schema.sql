CREATE TABLE IF NOT EXISTS capabilities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(90) NOT NULL
);

CREATE TABLE IF NOT EXISTS bootcamp_capabilities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_bootcamp BIGINT NOT NULL,
    id_capability BIGINT NOT NULL,
    CONSTRAINT fk_bootcamp_capabilities_capability FOREIGN KEY (id_capability) REFERENCES capabilities(id),
    CONSTRAINT uq_bootcamp_capability UNIQUE (id_bootcamp, id_capability)
)