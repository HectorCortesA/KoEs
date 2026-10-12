
package com.hector.koes.data

import com.hector.koes.model.*

object OracionData {

    private val tablaVerbos = listOf(
        ComparacionVerbo(
            "먹다 (comer)",
            "먹어요",
            "먹었어요",
            "먹을 거예요"
        ),
        ComparacionVerbo(
            "가다 (ir)",
            "가요",
            "갔어요",
            "갈 거예요"
        ),
        ComparacionVerbo(
            "마시다 (beber)",
            "마셔요",
            "마셨어요",
            "마실 거예요"
        ),
        ComparacionVerbo(
            "공부하다 (estudiar)",
            "공부해요",
            "공부했어요",
            "공부할 거예요"
        ),
        ComparacionVerbo(
            "보다 (ver)",
            "봐요",
            "봤어요",
            "볼 거예요"
        )
    )

    val oraciones = listOf(
        Oracion(
            id = 1,
            titulo = "Oración",
            descripcion = "En español normalmente usamos Sujeto + Verbo + Objeto. En coreano el orden básico es Sujeto + Objeto + Verbo.",
            estructuras = listOf(
                EstructuraOracion(
                    "Español",
                    listOf(
                        PalabraEstructura("Yo", "Sujeto"),
                        PalabraEstructura("Como", "Verbo"),
                        PalabraEstructura("arroz", "Objeto")
                    )
                ),
                EstructuraOracion(
                    "Coreano",
                    listOf(
                        PalabraEstructura("저는", "Tema"),
                        PalabraEstructura("밥을", "Objeto"),
                        PalabraEstructura("먹어요", "Verbo")
                    )
                )
            ),
            significados = listOf(
                SignificadoPalabra("저", "Jeo", "Yo"),
                SignificadoPalabra("는", "Neun", "Partícula de tema"),
                SignificadoPalabra("밥", "Bap", "Arroz / comida"),
                SignificadoPalabra("을", "Eul", "Partícula de objeto"),
                SignificadoPalabra("먹어요", "Meogeoyo", "Como / come")
            ),
            ejemplos = listOf(
                EjemploOracion("저는 물을 마셔요.", "Yo bebo agua."),
                EjemploOracion("저는 사과를 먹어요.", "Yo como una manzana."),
                EjemploOracion("저는 한국어를 공부해요.", "Yo estudio coreano."),
                EjemploOracion("저는 책을 읽어요.", "Yo leo un libro."),
                EjemploOracion("저는 학교에 가요.", "Yo voy a la escuela.")
            )
        ),

        Oracion(
            id = 2,
            titulo = "Oración sencilla",
            descripcion = "No todas las oraciones coreanas necesitan un objeto. Algunas expresan una idea completa con un tema y un predicado.",
            estructuras = listOf(
                EstructuraOracion(
                    "Coreano",
                    listOf(
                        PalabraEstructura("저는", "Tema"),
                        PalabraEstructura("학생이에요", "Predicado")
                    )
                )
            ),
            significados = listOf(
                SignificadoPalabra("저", "Jeo", "Yo"),
                SignificadoPalabra("는", "Neun", "Partícula de tema"),
                SignificadoPalabra("학생", "Haksaeng", "Estudiante"),
                SignificadoPalabra("이에요", "Iey o".replace(" ", ""), "Ser (forma cortés)")
            ),
            ejemplos = listOf(
                EjemploOracion("저는 학생이에요.", "Yo soy estudiante."),
                EjemploOracion("저는 선생님이에요.", "Yo soy profesor."),
                EjemploOracion("날씨가 좋아요.", "El clima es bueno."),
                EjemploOracion("비가 와요.", "Llueve."),
                EjemploOracion("저는 자요.", "Yo duermo.")
            )
        ),

        Oracion(
            id = 3,
            titulo = "Oración en tiempo presente",
            descripcion = "El presente expresa acciones actuales o habituales. Una forma cortés común se construye con la raíz verbal + 아요/어요.",
            estructuras = listOf(
                EstructuraOracion(
                    "Presente",
                    listOf(
                        PalabraEstructura("먹", "Raíz"),
                        PalabraEstructura("어요", "Terminación")
                    )
                )
            ),
            ejemplos = listOf(
                EjemploOracion("저는 밥을 먹어요.", "Yo como arroz."),
                EjemploOracion("저는 학교에 가요.", "Yo voy a la escuela."),
                EjemploOracion("저는 물을 마셔요.", "Yo bebo agua."),
                EjemploOracion("저는 공부해요.", "Yo estudio."),
                EjemploOracion("저는 영화를 봐요.", "Yo veo una película.")
            ),
            comparaciones = tablaVerbos
        ),

        Oracion(
            id = 4,
            titulo = "Oración en tiempo pasado",
            descripcion = "El pasado expresa acciones que ya ocurrieron. Una forma cortés común utiliza 았어요/었어요, con las contracciones correspondientes.",
            estructuras = listOf(
                EstructuraOracion(
                    "Pasado",
                    listOf(
                        PalabraEstructura("먹", "Raíz"),
                        PalabraEstructura("었어요", "Pasado")
                    )
                )
            ),
            ejemplos = listOf(
                EjemploOracion("저는 밥을 먹었어요.", "Yo comí arroz."),
                EjemploOracion("저는 학교에 갔어요.", "Yo fui a la escuela."),
                EjemploOracion("저는 물을 마셨어요.", "Yo bebí agua."),
                EjemploOracion("저는 공부했어요.", "Yo estudié."),
                EjemploOracion("저는 영화를 봤어요.", "Yo vi una película.")
            ),
            comparaciones = tablaVerbos
        ),

        Oracion(
            id = 5,
            titulo = "Oración en tiempo futuro",
            descripcion = "Una forma habitual de expresar acciones futuras es utilizar la raíz verbal + (으)ㄹ 거예요.",
            estructuras = listOf(
                EstructuraOracion(
                    "Futuro",
                    listOf(
                        PalabraEstructura("먹", "Raíz"),
                        PalabraEstructura("을 거예요", "Futuro")
                    )
                )
            ),
            ejemplos = listOf(
                EjemploOracion("저는 밥을 먹을 거예요.", "Yo comeré arroz."),
                EjemploOracion("저는 학교에 갈 거예요.", "Yo iré a la escuela."),
                EjemploOracion("저는 물을 마실 거예요.", "Yo beberé agua."),
                EjemploOracion("저는 공부할 거예요.", "Yo estudiaré."),
                EjemploOracion("저는 영화를 볼 거예요.", "Yo veré una película.")
            ),
            comparaciones = tablaVerbos
        ),

        Oracion(
            id = 6,
            titulo = "Partículas 은/는",
            descripcion = "은 y 는 indican el tema de una oración. 은 se utiliza después de una consonante final y 는 después de una vocal.",
            significados = listOf(
                SignificadoPalabra("은", "Eun", "Tema después de consonante"),
                SignificadoPalabra("는", "Neun", "Tema después de vocal"),
                SignificadoPalabra("저는", "Jeoneun", "En cuanto a mí"),
                SignificadoPalabra("학생은", "Haksaengeun", "En cuanto al estudiante")
            ),
            ejemplos = listOf(
                EjemploOracion("저는 학생이에요.", "Yo soy estudiante."),
                EjemploOracion("오늘은 추워요.", "Hoy hace frío."),
                EjemploOracion("이것은 책이에요.", "Esto es un libro."),
                EjemploOracion("저는 한국어를 공부해요.", "Yo estudio coreano.")
            )
        ),

        Oracion(
            id = 7,
            titulo = "Partículas 이/가",
            descripcion = "이 y 가 marcan el sujeto gramatical. 이 se utiliza después de una consonante final y 가 después de una vocal.",
            significados = listOf(
                SignificadoPalabra("이", "I", "Sujeto después de consonante"),
                SignificadoPalabra("가", "Ga", "Sujeto después de vocal"),
                SignificadoPalabra("학생이", "Haksaengi", "Estudiante (sujeto)"),
                SignificadoPalabra("날씨가", "Nalssiga", "Clima (sujeto)")
            ),
            ejemplos = listOf(
                EjemploOracion("학생이 와요.", "El estudiante viene."),
                EjemploOracion("날씨가 좋아요.", "El clima es bueno."),
                EjemploOracion("비가 와요.", "Llueve."),
                EjemploOracion("고양이가 자요.", "El gato duerme.")
            )
        ),

        Oracion(
            id = 8,
            titulo = "Partículas 을/를",
            descripcion = "을 y 를 indican el objeto directo. 을 se utiliza después de una consonante final y 를 después de una vocal.",
            significados = listOf(
                SignificadoPalabra("을", "Eul", "Objeto después de consonante"),
                SignificadoPalabra("를", "Reul", "Objeto después de vocal"),
                SignificadoPalabra("밥을", "Babeul", "Arroz (objeto)"),
                SignificadoPalabra("사과를", "Sagwareul", "Manzana (objeto)")
            ),
            ejemplos = listOf(
                EjemploOracion("저는 밥을 먹어요.", "Yo como arroz."),
                EjemploOracion("저는 물을 마셔요.", "Yo bebo agua."),
                EjemploOracion("저는 사과를 먹어요.", "Yo como una manzana."),
                EjemploOracion("저는 책을 읽어요.", "Yo leo un libro.")
            )
        )
    )
}
