# BlockyFix

BlockyFix é um plugin para servidores Minecraft Beta 1.7.3 que corrige o bug relacionado à velocidade de quebra de blocos ao usar ferramentas como picareta e machado. O plugin permite personalizar, por meio de configuração, quais blocos devem ser quebrados de forma instantânea, tornando o servidor mais equilibrado e livre de inconsistências presentes na versão nativa do jogo.

# Funcionalidades

- Seleção de blocos instantaneamente quebráveis por ID e DATA usando `config.yml`
- Suporte à configuração por ferramenta: machado (`axes`) ou picareta (`pickaxes`)
- Drop personalizado para blocos quebrados (por exemplo, laje dupla vira duas lajes simples, pedra vira pedregulho, etc)
- Compatível com Uberbukkit 2.0.2

# Instalação

- Compile o projeto com Maven (`mvn clean package`)
- Coloque o arquivo gerado `.jar` em `/plugins` do servidor Minecraft Beta 1.7.3
- O plugin gera automaticamente o `config.yml`, que pode ser editado conforme sua necessidade
- Reinicie o servidor

# Configuração

O arquivo `config.yml` permite definir, por ID e DATA, quais blocos serão instantaneamente quebrados por qual ferramenta. Confira IDs usando plugins como [BlockInspector](https://github.com/andradecore/BlockInspector).

## Reportar bugs ou requisitar features

Reporte bugs em [issues](https://github.com/andradecore/BlockyFix/issues).

## Contato:

- Discord: https://discord.gg/tthPMHrP
