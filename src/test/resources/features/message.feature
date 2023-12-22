# language: pt

  Caracteristica: Message

    Cenario: Register Message
      Quando eu registrar uma mensagem
      Entao o valor deve ser registrado no sistema
      E o valor deve ser apresentado

    Cenario: Find Message
      Dado que uma mensagem já foi publicada
      Quando efetuar a busca de mensagem
      Entao A mensagem é exibida com sucesso

#    Cenario: Update Message
#      Dado que uma mensagem ja foi publicada
#      Quando efetuar a requisicao para alterar
#      Entao a mensagem é alterada com sucesso
#      E o novo valor é apresentado
#
#    Cenario: Delete Message
#      Dado que uma mensagem ja publicada
#      Quando requisitar a remoção da mensagem
#      Entao a mensagem é removida com sucesso