# Minecraft-Nukkit-TreeCapitator
The plugin TreeCapitator for Nukkit lets you cut down trees by a few hits

Данный плагин позволит вам рубить деревья разом! Поддерживаются все дервья, включая тропическую секвойю и темный дуб.

![alt text](http://file-minecraft.com/wp-content/uploads/2014/07/Tree-Capitator-Mod.jpg)

<h3>Config</h3>
<b>config.yml</b>

~~~
#TreeCapitator
#made by Fleynaro v1.0
#GitHub: https://github.com/Fleynaro/Minecraft-Nukkit-TreeCapitator

#Чтобы срубить всё разом, нужен топор
only-axe: true

#Насколько единиц уменьшается прочность топора при рубке.
#Например, прочность деревянного топора - 60, а каменного - 132.
axe-durability-damage: 5

#Коэффициент кол-ва рубок одного блока: ((кол-во рубок) * hit-to-cut-down)
#Например, значение 3.0 увеличит необходимое кол-во рубок в 2 раза, так как сейчас стоит 1.5
hit-to-cut-down: 1.5

#Кол-во блоков дерева до земли, чтобы срубить дерево разом
count-down-blocks: 1

#Проверять снизу на блок земли
check-dirt-down: true
~~~
