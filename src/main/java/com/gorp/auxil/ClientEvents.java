package com.gorp.auxil;

import com.gorp.auxil.content.logistics.radiant_chassis.RadiantChassisHandler;
import com.gorp.auxil.content.logistics.shadow_chassis.ShadowChassisHandler;
import com.gorp.auxil.foundation.config.ConfigScreen;
import com.simibubi.create.foundation.config.AllConfigs;
import com.simibubi.create.foundation.config.ui.BaseConfigScreen;
import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import com.simibubi.create.foundation.gui.TextStencilElement;
import com.simibubi.create.foundation.gui.widgets.BoxWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ClientEvents {

    private static final String itemPrefix = "item." + Auxiliaries.MODID;
    private static final String blockPrefix = "block." + Auxiliaries.MODID;

    @SubscribeEvent
    public static void addToItemTooltip(ItemTooltipEvent event) {
        /*if (!AllConfigs.CLIENT.tooltips.get())
            return;
        if (event.getPlayer() == null)
            return;

        ItemStack stack = event.getItemStack();
        String translationKey = stack.getItem()
                .getDescriptionId(stack);
        if (!translationKey.startsWith(itemPrefix) && !translationKey.startsWith(blockPrefix))
            return;

        if (TooltipHelper.hasTooltip(stack, event.getPlayer())) {
            List<ITextComponent> itemTooltip = event.getToolTip();
            List<ITextComponent> toolTip = new ArrayList<>();
            toolTip.add(itemTooltip.remove(0));
            TooltipHelper.getTooltip(stack)
                    .addInformation(toolTip);
            itemTooltip.addAll(0, toolTip);
        }

        if (stack.getItem() instanceof BlockItem) {
            BlockItem item = (BlockItem)stack.getItem();
            if (item.getBlock() instanceof IRotate || item.getBlock() instanceof EngineBlock) {
                List<ITextComponent> kineticStats = ItemDescription.getKineticStats(item.getBlock());
                if (!kineticStats.isEmpty()) {
                    event.getToolTip().add(new StringTextComponent(""));
                    event.getToolTip().addAll(kineticStats);
                }
            }
        }

        PonderTooltipHandler.addToTooltip(event.getToolTip(), stack);*/
    }
    
    @SubscribeEvent
    public static void onGuiEvent(net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent.Post event) {
        if(event.getGui() instanceof BaseConfigScreen) {
            BaseConfigScreen screen = (BaseConfigScreen) event.getGui();
            try {
                Class<BaseConfigScreen> baseConfigClass = BaseConfigScreen.class;
                Field spec = baseConfigClass.getDeclaredField("clientSpec");
                spec.setAccessible(true);
                if (spec.get(screen) == AllConfigs.CLIENT.specification) {
                    try {
                        Class<AbstractSimiScreen> clazz = AbstractSimiScreen.class;
                        Field field = clazz.getDeclaredField("widgets");
                        field.setAccessible(true);
                        List<Widget> widgets = new ArrayList<>();
                        widgets = (List<Widget>) field.get(screen);
                        BoxWidget caWidget;
                        TextStencilElement commonText = new TextStencilElement(Minecraft.getInstance().font, "config").centered(true, true);
                        widgets.add(caWidget = new BoxWidget(screen.width / 2 - 100, screen.height / 2 + 60, 200, 16).showingElement(commonText));
                        Class<BaseConfigScreen> clazz2 = BaseConfigScreen.class;
                        Method method = clazz2.getDeclaredMethod("linkTo", Screen.class);
                        method.setAccessible(true);
                        caWidget.withCallback(() -> {
                            try {
                                method.invoke(screen, ConfigScreen.forAux(screen));
                            } catch (Exception e) {
                            }
                        });
                        commonText.withElementRenderer(BoxWidget.gradientFactory.apply(caWidget));
                    } catch (Exception e) {
                    }
                }
            } catch (Exception e) { }
        }
    }
    
    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent event) {
        World world = Minecraft.getInstance().level;
        if (event.phase == TickEvent.Phase.START)
            return;

        if (!isGameActive())
            return;

        RadiantChassisHandler.tick();
        ShadowChassisHandler.tick();
    }

    protected static boolean isGameActive() {
        return !(Minecraft.getInstance().level == null || Minecraft.getInstance().player == null);
    }

}
