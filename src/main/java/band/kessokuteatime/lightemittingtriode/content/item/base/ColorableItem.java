package band.kessokuteatime.lightemittingtriode.content.item.base;

import band.kessokuteatime.lightemittingtriode.LightEmittingTriode;
import band.kessokuteatime.lightemittingtriode.content.item.base.extension.WithInjectedTooltip;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;

import java.util.List;

public interface ColorableItem extends WithInjectedTooltip {
    DyeColor getDyeColor();

    String getParentTranslationKey();

    default int getDisplayColor() {
        return LightEmittingTriode.mapColorRange(getDyeColor().getSignColor(), 0x55, 0);
    }

    default int getDisplayBackgroundColor() {
        return LightEmittingTriode.mapColorRange(getDyeColor().getSignColor(), 0x55, 0x8A);
    }

    default Text getName() {
        return Text.translatable(
                        getParentTranslationKey(),
                        Text.translatable(LightEmittingTriode.idString("color", getDyeColor().getName()))
                )
                .styled(style -> style.withColor(getDisplayColor()));
    }

    default Text getColorTag() {
        return Text.literal(String.format("#%06X", LightEmittingTriode.getColorFromDyeColor(getDyeColor())))
                .styled(style -> style
                        .withItalic(true)
                        .withColor(getDisplayBackgroundColor())
                );
    }

    @Override
    default List<Text> getInjectedTooltips() {
        return List.of(getColorTag());
    }
}
